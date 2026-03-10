import { createIcon } from '@/utils/IconUtil';
import { MenuDataItem } from '@ant-design/pro-components';
import { request, Outlet } from '@umijs/max';
import React, { lazy, Suspense, createElement } from 'react';

// 简单的 Loading 组件
const PageLoading = () => <div style={{ padding: 20, textAlign: 'center' }}>页面加载中...</div>;

let remoteMenu: any = null;

export function getRemoteMenu() {
  return remoteMenu;
}

export function setRemoteMenu(data: any) {
  remoteMenu = data;
}

/**
 * 生成唯一的 Key
 */
function generateUniqueKey(path: string, name: string, index: number): string {
  const safePath = path.replace(/\/+/g, '_').replace(/^_/, '') || 'root';
  const safeName = (name || '').replace(/[^a-zA-Z0-9]/g, '');
  return `menu_${safePath}_${safeName}_${index}`;
}

/**
 * 【核心修复】标准化组件路径
 * 目标：将后端的各种写法统一为相对于 src/pages 的路径
 * 示例输入: "system/user", "system/user/index", "system/user.tsx", "/system/user"
 * 示例输出: "./system/user"
 */
function normalizeComponentPath(component: string | undefined): string | null {
  if (!component) return null;

  let path = component.trim(); // 去除首尾空格

  // 1. 去除文件后缀
  path = path.replace(/\.(vue|tsx|jsx|ts|js)$/, '');

  // 2. 去除末尾的 /index
  if (path.endsWith('/index')) {
    path = path.substring(0, path.length - 6);
  }

  // 3. 去除前导斜杠
  if (path.startsWith('/')) {
    path = path.substring(1);
  }

  // 4. 确保以 ./ 开头
  if (!path.startsWith('./')) {
    path = `./${path}`;
  }

  return path;
}

/**
 * 创建动态导入的 React Element
 * 增加错误捕获和日志
 */
function createLazyElement(componentPath: string | null, routePath: string) {
  if (!componentPath) {
    console.warn(`[Route Error] No component path for route: ${routePath}`);
    return null;
  }

  const cleanPath = componentPath.replace(/^\.\//, '');
  const importPath = `@/pages/${cleanPath}`;

  console.log(`[Route Debug] Creating element for ${routePath} -> Importing: ${importPath}`);

  try {
    const Component = lazy(() => import(/* webpackChunkName: "[request]" */ `${importPath}`));

    return createElement(
      Suspense,
      { fallback: <PageLoading /> },
      createElement(Component)
    );
  } catch (e) {
    console.error(`[Route Error] Failed to create lazy component for ${importPath}:`, e);
    return createElement('div', { style: { color: 'red', padding: 20 } },
      `Error loading page: ${routePath}. Check if file exists at src/pages/${cleanPath}/index.tsx`
    );
  }
}

/**
 * 递归构建路由树
 */
function buildRoutes(menuItems: any[], parentPath: string = '', depthIndex: number[] = []): any[] {
  const resultRoutes: any[] = [];

  if (!menuItems) return resultRoutes;

  menuItems.forEach((item, index) => {
    // 1. 路径处理
    let currentPath = item.path || '';
    let finalPath = currentPath;

    if (parentPath && parentPath !== '/') {
      if (!currentPath.startsWith(parentPath)) {
        const p = parentPath.replace(/\/$/, '');
        const c = currentPath.replace(/^\//, '');
        finalPath = `${p}/${c}`;
      }
    } else if (!currentPath.startsWith('/') && currentPath !== '') {
      finalPath = '/' + currentPath;
    }
    finalPath = finalPath.replace(/\/+/g, '/') || '/';

    const children = item.children || item.routes;
    const hasChildren = children && children.length > 0;

    // 判断是否是布局节点
    const isLayout = item.component === 'Layout' || item.component === 'ParentView' || !item.component;

    // 2. 决定 Element
    let element = null;

    if (hasChildren) {
      // 父节点必须有 Outlet
      element = <Outlet />;
      console.log(`[Route Debug] Parent node ${finalPath} assigned Outlet.`);
    } else if (!isLayout && item.component) {
      // 叶子节点：尝试加载组件
      const compPath = normalizeComponentPath(item.component);
      console.log(`[Route Debug] Leaf node ${finalPath}, raw component: "${item.component}", normalized: "${compPath}"`);

      if (compPath) {
        element = createLazyElement(compPath, finalPath);
      } else {
        console.error(`[Route Error] Leaf node ${finalPath} has invalid component config: "${item.component}"`);
        // 即使出错，也给一个提示元素，避免完全空白
        element = createElement('div', {}, `Missing component for ${finalPath}`);
      }
    } else if (isLayout && !hasChildren) {
       // 既是 Layout 又没有子节点，理论上不应该访问，但也给个 Outlet 防止报错
       element = <Outlet />;
    }

    // 如果上面都没赋上值，说明配置有问题
    if (!element && !hasChildren) {
       console.warn(`[Route Warning] Route ${finalPath} has no element and no children. It will render blank.`);
    }

    // 3. 生成唯一 Key
    const currentDepthIndex = [...depthIndex, index];
    const uniqueKey = generateUniqueKey(finalPath, item.name || item.meta?.title, currentDepthIndex.join('-'));

    // 4. 构建对象
    const routeObj: any = {
      path: finalPath,
      name: item.name || item.meta?.title || item.meta?.parmName,
      icon: item.icon || item.meta?.icon,
      hideInMenu: !!item.hidden,
      authority: item.perms,
      element: element, // 必须赋值
      key: uniqueKey,
      children: hasChildren ? buildRoutes(children, finalPath, currentDepthIndex) : undefined,
    };

    if (routeObj.children) {
      routeObj.routes = routeObj.children;
    }

    resultRoutes.push(routeObj);
  });

  return resultRoutes;
}

export function patchRouteWithRemoteMenus(routes: any[]) {
  if (!remoteMenu || remoteMenu.length === 0) {
    console.warn('[Session] No remote menu data.');
    return;
  }

  let rootRoute = routes.find((r) => r.id === 'ant-design-pro-layout');
  if (!rootRoute) {
    rootRoute = routes.find((r) => r.path === '/');
  }

  if (!rootRoute) {
    console.error('[Session] Cannot find root route.');
    return;
  }

  const DYNAMIC_MARKER = '__is_dynamic_route__';

  // 清洗旧数据
  const staticRoutes = (rootRoute.children || []).filter((r: any) => !r[DYNAMIC_MARKER]);

  // 构建新数据
  console.log('[Session] Building routes from remote menu...', remoteMenu);
  const newDynamicRoutes = buildRoutes(remoteMenu, '', []);

  const taggedDynamicRoutes = newDynamicRoutes.map(r => ({ ...r, [DYNAMIC_MARKER]: true }));

  const finalRoutes = [...staticRoutes, ...taggedDynamicRoutes];

  rootRoute.children = finalRoutes;
  rootRoute.routes = finalRoutes;

  console.log(`[Session] Routes patched. Total: ${finalRoutes.length}`);

  // 打印所有生成的路由路径和是否有 element，方便调试
  finalRoutes.forEach(r => {
    if (r.path.includes('user')) {
       console.log(`[Check] Route: ${r.path}, Has Element: ${!!r.element}, Has Children: ${!!r.children}`);
    }
  });
}

/** 获取用户信息 */
export async function getUserInfo(options?: Record<string, any>) {
  return request<API.UserInfoResult>('/api/getInfo', {
    method: 'GET',
    ...(options || {}),
  });
}

export async function refreshToken() {
  return request('/api/auth/refresh', { method: 'post' });
}

export async function getRouters(): Promise<any> {
  return request('/api/getRouters');
}

export function convertCompatRouters(childrens: API.RoutersMenuItem[]): any[] {
  return buildRoutes(childrens, '', []);
}

export async function getRoutersInfo(): Promise<MenuDataItem[]> {
  return getRouters().then((res) => {
    if (res.code === 200) {
      return convertCompatRouters(res.data);
    }
    return [];
  });
}

export function getMatchMenuItem(path: string, menuData: MenuDataItem[] | undefined): MenuDataItem[] {
  if (!menuData) return [];
  let items: MenuDataItem[] = [];
  menuData.forEach((item) => {
    if (item.path) {
      if (item.path === path) {
        items.push(item);
        return;
      }
      if (path.startsWith(item.path + '/')) {
        if (item.routes) {
          const subpath = path.substring(item.path.length + 1);
          items = items.concat(getMatchMenuItem(subpath, item.routes));
        }
      }
    }
  });
  return items;
}
