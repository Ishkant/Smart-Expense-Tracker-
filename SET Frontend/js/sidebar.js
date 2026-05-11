// sidebar.js — dynamically injects sidebar and sets active nav
function renderSidebar(activePage) {
  const user = getUser();
  const initial = user?.name ? user.name[0].toUpperCase() : '?';

  const navItems = [
    { id: 'dashboard',  href: 'dashboard.html',  icon: '📊', label: 'Dashboard' },
    { id: 'expenses',   href: 'expenses.html',   icon: '💸', label: 'Expenses' },
    { id: 'income',     href: 'income.html',     icon: '💰', label: 'Income' },
    { id: 'budgets',    href: 'budgets.html',    icon: '🎯', label: 'Budgets' },
    { id: 'analytics',  href: 'analytics.html',  icon: '📈', label: 'Analytics' },
    { id: 'profile',    href: 'profile.html',    icon: '⚙️', label: 'Settings' },
  ];

  const navHTML = navItems.map(item => `
    <a href="${item.href}" class="nav-item ${activePage === item.id ? 'active' : ''}">
      <span class="nav-icon">${item.icon}</span>
      <span>${item.label}</span>
    </a>
  `).join('');

  const sidebarHTML = `
    <div class="sidebar-logo">
      <div class="logo-text">💰 SpendWise</div>
      <div class="logo-sub">Smart Expense Tracker</div>
    </div>
    <nav class="sidebar-nav">
      <div class="nav-section-label">Main</div>
      ${navHTML}
    </nav>
    <div class="sidebar-user">
      <div class="user-avatar">${initial}</div>
      <div class="user-info">
        <div class="user-name">${user?.name || 'User'}</div>
        <div class="user-email">${user?.email || ''}</div>
      </div>
      <button onclick="logout()" title="Logout" style="background:none;border:none;color:var(--text3);cursor:pointer;font-size:1.1rem;padding:4px;transition:var(--transition);" onmouseover="this.style.color='var(--red)'" onmouseout="this.style.color='var(--text3)'">⏏</button>
    </div>`;

  const sidebar = document.querySelector('.sidebar');
  if (sidebar) sidebar.innerHTML = sidebarHTML;
}
