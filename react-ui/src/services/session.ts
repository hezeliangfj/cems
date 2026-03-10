import { createIcon } from '@/utils/IconUtil';
import { MenuDataItem } from '@ant-design/pro-components';
import { request } from '@umijs/max';
import React from 'react';

// 全局存储 routeComponents，供 patchRoutes 使用
let globalRouteComponents: any = null;

export function setRouteComponents(components: any) {
  globalRouteComponents = components;
}

export function getRouteComponents() {
  return globalRouteComponents;
}

let remoteMenu: any = null;

export function getRemoteMenu() {
  return remoteMenu;
}

export function setRemoteMenu(data: any) {
  remoteMenu = data;
}

/**
 * 处理组件路径：确保格式符合 Umi 动态路由约定
 * 目标：将后端返回的 "system/user" 转换为 "./system/user" (不带 pages 前缀)
 * 后续的 patchRouteItems 会负责加上 "./pages/" 前缀
 */
function normalizeComponentPath(component: string): string {
  // 1. 空值处理
  if (!component || typeof component !== 'string') {
    return '';
  }

  let path = component.trim();

  // 2. 特殊组件直接返回 (Layout, ParentView 等不需要路径转换)
  if (path === 'Layout' || path === 'ParentView' || path === 'UserLayout') {
    return path;
  }

  // 3. 去除文件后缀 (.tsx, .ts, .jsx, .js, .vue)
  path = path.replace(/\.(tsx|ts|jsx|js|vue)$/, '');

  // 4. 去除开头的斜杠 /
  path = path.replace(/^\/+/, '');

  // 5. 去除开头的 ./ (如果已有，先去掉再重新加，保证格式统一)
  path = path.replace(/^\.\//, '');

  // 6. 【关键步骤】去除可能存在的目录前缀 (pages/, views/, src/)
  // 因为我们要统一在 patchRouteItems 中处理，这里只保留相对路径部分
  if (path.startsWith('pages/')) {
    path = path.substring(6);
  } else if (path.startsWith('views/')) {
    path = path.substring(6);
  } else if (path.startsWith('src/')) {
    path = path.substring(4);
  }

  // 7. 加上 ./ 前缀，这是基础格式
  // 最终结果：system/user -> ./system/user
  return `./${path}`;
}

/**
 * 递归修补路由树
 */
function patchRouteItems(route: any, menu: any[], marker?: string, parentPath: string = '', routeComponents?: any) {
  for (const menuItem of menu) {
    // 拼接完整的 path（相对于根目录）
    // 注意处理 parentPath 为空或为 '/' 的情况
    const cleanParent = parentPath === '/' ? '' : parentPath;
    const cleanChild = menuItem.path?.startsWith('/') ? menuItem.path.substring(1) : menuItem.path;
    const fullPath = cleanParent ? `${cleanParent}/${cleanChild}` : `/${cleanChild}`;

    // 处理 Layout 或 父级目录 (没有具体 component 或有 children)
    const isLayout = menuItem.component === 'Layout' || menuItem.component === 'ParentView' || !menuItem.component;
    const childRoutes = menuItem.routes || menuItem.children;
    const hasChildren = childRoutes && childRoutes.length > 0;

    if (isLayout || hasChildren) {
      // 只用 routes，不同时用 children
      if (!route.routes) route.routes = [];

      let hasItem = false;
      let newItem = null;

      // 查找是否已存在该路径的路由
      for (const routeChild of route.routes) {
        if (routeChild.path === fullPath) {
          hasItem = true;
          newItem = routeChild;
          break;
        }
      }

      if (!hasItem) {
        newItem = {
          path: fullPath,
          name: menuItem.name || menuItem.meta?.title,
          icon: menuItem.icon || menuItem.meta?.icon,
          // 如果是布局节点，component 设为 Layout 或不设 (Umi 会自动处理 Outlet)
          component: isLayout ? 'Layout' : undefined,
          routes: [],
        };
        // 添加标记
        if (marker) {
          (newItem as any)[marker] = true;
        }
        route.routes.push(newItem);
      } else {
        // 复用现有路由时，也要设置 component（如果原来是 Layout 类型）
        if (isLayout && !newItem.component) {
          newItem.component = 'Layout';
        }
        // 更新 name
        if (menuItem.name) {
          newItem.name = menuItem.name;
        }
      }

      // 递归处理子菜单，传递当前完整路径
      if (newItem && hasChildren) {
        patchRouteItems(newItem, childRoutes, marker, fullPath, routeComponents);
      }
    } else {
      // 处理具体页面组件 (叶子节点)
      if (menuItem.path && menuItem.component) {
        // 检查是否已经存在相同路径的路由
        let routeExists = false;
        if (route.routes) {
          for (const existingRoute of route.routes) {
            if (existingRoute.path === fullPath) {
              routeExists = true;
              break;
            }
          }
        }

        if (!routeExists) {
          if (!route.routes) route.routes = [];

          // 1. 获取标准化路径 (例如: ./system/user)
          const normalizedPath = normalizeComponentPath(menuItem.component);

          // 2. 【核心修复】构建正确的 component 路径
          // Umi 的 routeComponents 映射表 key 通常是 './pages/xxx'
          // 所以我们需要确保 finalComponentPath 包含 'pages/'

          let finalComponentPath = normalizedPath;

          if (normalizedPath !== 'Layout' && normalizedPath !== 'ParentView' && normalizedPath !== 'UserLayout') {
             // 去掉开头的 ./ 以便重新拼接
             const cleanPath = normalizedPath.replace(/^\.\//, '');

             // 如果路径中不包含 pages/ 或 views/，则默认加上 pages/
             if (!cleanPath.startsWith('pages/') && !cleanPath.startsWith('views/')) {
               finalComponentPath = `./pages/${cleanPath}`;
             } else {
               // 如果已经有 pages/，只需确保有 ./
               finalComponentPath = `./${cleanPath}`;
             }
          }

          // 生成唯一的 ID
          const routeId = `dynamic_${fullPath.replace(/\//g, '_').replace(/^_/, '')}`;

          const newRoute = {
            id: routeId,
            path: fullPath,
            name: menuItem.name || menuItem.meta?.title,
            icon: menuItem.icon || menuItem.meta?.icon,
            // 使用修正后的路径，这样能匹配 Umi 自动生成的 routeComponents
            component: finalComponentPath,
          };

          console.log('[Debug] Added dynamic route:', {
            id: routeId,
            path: fullPath,
            raw: menuItem.component,
            normalized: normalizedPath,
            final: finalComponentPath
          });

          // 添加标记
          if (marker) {
            (newRoute as any)[marker] = true;
          }

          route.routes.push(newRoute);
        }
      }
    }
  }
}

export function patchRouteWithRemoteMenus(routes: any, routeComponents?: any) {
  if (remoteMenu === null) {
    console.warn('[Session] Remote menu not loaded yet.');
    return;
  }

  // 如果没有传入 routeComponents，尝试从全局获取
  const effectiveRouteComponents = routeComponents || globalRouteComponents;

  // 防止重复注入
  const PATCHED_MARKER = '__menu_patched__';
  if ((routes as any)[PATCHED_MARKER]) {
    console.log('[Session] Routes already patched, skipping.');
    return;
  }

  let proLayout = null;
  for (const routeItem of routes) {
    if (routeItem.id === 'ant-design-pro-layout') {
      proLayout = routeItem;
      break;
    }
  }

  if (!proLayout) {
    console.error('[Session] Cannot find ant-design-pro-layout route.');
    return;
  }

  // 初始化 routes 数组
  if (!proLayout.routes) proLayout.routes = [];

  // 过滤掉之前动态添加的路由，保留静态路由
  const staticRoutes = proLayout.routes.filter((r: any) => !r[PATCHED_MARKER]);
  proLayout.routes = staticRoutes;

  // 开始修补
  patchRouteItems(proLayout, remoteMenu, PATCHED_MARKER, '', effectiveRouteComponents);

  // 标记已处理
  (routes as any)[PATCHED_MARKER] = true;

  // 打印简要信息（递归打印所有层级的路由）
  const printRoutes = (routes: any[], level = 0) => {
    routes.forEach((r: any) => {
      const indent = '  '.repeat(level);
      console.log(`${indent}path: ${r.path}, name: ${r.name}, component: ${r.component}`);
      if (r.routes && r.routes.length > 0) {
        printRoutes(r.routes, level + 1);
      }
    });
  };
  console.log('Routes patched successfully:');
  printRoutes(proLayout.routes);
}

/** 获取当前的用户 GET /api/getUserInfo */
export async function getUserInfo(options?: Record<string, any>) {
  return request<API.UserInfoResult>('/api/getInfo', {
    method: 'GET',
    ...(options || {}),
  });
}

// 刷新方法
export async function refreshToken() {
  return request('/api/auth/refresh', {
    method: 'post',
  });
}

export async function getRouters(): Promise<any> {
  return request('/api/getRouters');
}

// 兼容模式路由转换 (如果主流程使用 patchRouteWithRemoteMenus，此函数可能作为备用)
export function convertCompatRouters(childrens: API.RoutersMenuItem[]): any[] {
  return childrens.map((item: API.RoutersMenuItem) => {
    let path = item.path;
    if (item.children) {
      item.children.forEach((child: any) => {
        if (child.path.startsWith(item.path + '/')) {
          child.path = child.path.substring(item.path.length + 1);
        }
      });
    }

    const fixComponentPath = (component: string) => {
      if (component === 'Layout') return 'Layout';
      const normalized = normalizeComponentPath(component);
      // 同样需要在这里加上 pages/ 前缀以匹配 Umi
      if (normalized.startsWith('./')) {
        const clean = normalized.substring(2);
        if (!clean.startsWith('pages/') && !clean.startsWith('views/')) {
          return `./pages/${clean}`;
        }
      }
      return normalized;
    };

    return {
      path: path,
      name: item.meta?.parmName,
      routes: item.children ? convertCompatRouters(item.children) : undefined,
      hideChildrenInMenu: item.hidden,
      hideInMenu: item.hidden,
      component: item.component === 'Layout' ? 'Layout' : fixComponentPath(item.component),
      authority: item.perms,
    };
  });
}

export async function getRoutersInfo(): Promise<MenuDataItem[]> {
  return getRouters().then((res) => {
    if (res.code === 200) {
      return convertCompatRouters(res.data);
    } else {
      return [];
    }
  });
}

export function getMatchMenuItem(
  path: string,
  menuData: MenuDataItem[] | undefined,
): MenuDataItem[] {
  if (!menuData) return [];
  let items: MenuDataItem[] = [];
  menuData.forEach((item) => {
    if (item.path) {
      if (item.path === path) {
        items.push(item);
        return;
      }
      if (path.length >= item.path?.length) {
        const exp = `${item.path}/*`;
        if (path.match(exp)) {
          if (item.routes) {
            const subpath = path.substr(item.path.length + 1);
            const subItem: MenuDataItem[] = getMatchMenuItem(subpath, item.routes);
            items = items.concat(subItem);
          } else {
            const paths = path.split('/');
            if (paths.length >= 2 && paths[0] === item.path && paths[1] === 'index') {
              items.push(item);
            }
          }
        }
      }
    }
  });
  return items;
}
