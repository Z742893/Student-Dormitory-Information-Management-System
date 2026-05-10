<template>
  <el-container class="layout-shell">
    <el-aside width="224px" class="layout-aside">
      <div class="layout-logo">管理员端</div>
      <el-menu
        class="layout-menu"
        :default-active="$route.path"
        router
        background-color="transparent"
        :text-color="'var(--app-sidebar-text)'"
        :active-text-color="'var(--app-sidebar-active)'"
      >
        <el-menu-item
          v-for="item in adminSidebar"
          :key="item.index"
          :index="item.index"
        >
          <LineIcon class="layout-menu-icon" :name="item.icon" :size="18" />
          <span>{{ item.title }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container class="layout-body">
      <el-header height="64px" class="layout-header">
        <div class="layout-header-inner">
          <div class="layout-title-wrap">
            <span class="layout-title">学生宿舍信息管理系统</span>
            <span class="layout-subtitle">管理端</span>
          </div>
          <el-button type="primary" plain class="layout-logout-btn" @click="logout">
            <LineIcon name="log-out" :size="16" />
            <span>退出登录</span>
          </el-button>
        </div>
      </el-header>
      <el-main class="layout-main">
        <div class="layout-main-inner admin-app">
          <router-view />
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter } from "vue-router";
import LineIcon from "../components/LineIcon.vue";
import { ADMIN_SIDEBAR } from "../constants/sidebarNav.js";

const router = useRouter();
const adminSidebar = ADMIN_SIDEBAR;

const logout = () => {
  localStorage.removeItem("token");
  router.push("/login");
};
</script>

<style scoped>
.layout-shell {
  height: 100vh;
  background: var(--app-bg);
}

.layout-aside {
  background: var(--app-sidebar-bg);
  border-right: 1px solid var(--app-sidebar-border);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.layout-logo {
  flex-shrink: 0;
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: var(--app-text);
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 0.06em;
  border-bottom: 1px solid var(--app-sidebar-border);
  background: var(--sky-50);
}

.layout-menu {
  flex: 1;
  border-right: none !important;
  padding: 12px 8px 20px;
  overflow-y: auto;
}

.layout-menu :deep(.el-menu-item) {
  border-radius: 8px;
  margin: 2px 8px;
  height: 42px;
  line-height: 42px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.layout-menu-icon {
  flex-shrink: 0;
  opacity: 0.92;
}

.layout-menu :deep(.el-menu-item.is-active) .layout-menu-icon {
  opacity: 1;
}

.layout-logout-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.layout-menu :deep(.el-menu-item.is-active) {
  background: var(--app-menu-active-bg) !important;
  font-weight: 600;
}

.layout-menu :deep(.el-menu-item:hover) {
  background: var(--app-menu-hover-bg) !important;
}

.layout-body {
  flex-direction: column;
  min-width: 0;
}

.layout-header {
  padding: 0 24px;
  background: var(--app-header-bg);
  box-shadow: var(--app-header-shadow);
  display: flex;
  align-items: center;
}

.layout-header-inner {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.layout-title-wrap {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.layout-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--app-text);
  letter-spacing: 0.02em;
}

.layout-subtitle {
  font-size: 12px;
  color: var(--app-text-muted);
}

.layout-main {
  padding: var(--app-main-padding);
  background: var(--app-bg);
  overflow: auto;
}

.layout-main-inner {
  max-width: 1400px;
  margin: 0 auto;
}
</style>
