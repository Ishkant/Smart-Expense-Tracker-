// =============================================
//  API Configuration — points to Spring Boot
// =============================================
const API_BASE = 'http://localhost:8080/api';

const API = {
  // Auth
  REGISTER:   `${API_BASE}/auth/register`,
  LOGIN:      `${API_BASE}/auth/login`,
  PROFILE:    `${API_BASE}/auth/profile`,

  // Expenses
  EXPENSES:           `${API_BASE}/expenses`,
  EXPENSE_BY_ID: (id) => `${API_BASE}/expenses/${id}`,
  EXPENSES_MONTH:     `${API_BASE}/expenses/month`,
  EXPENSES_CATEGORY:  (cat) => `${API_BASE}/expenses/category/${encodeURIComponent(cat)}`,
  EXPENSES_SEARCH:    `${API_BASE}/expenses/search`,
  EXPENSE_CATEGORIES: `${API_BASE}/expenses/categories`,

  // Budgets & Analytics
  BUDGETS:          `${API_BASE}/budgets`,
  BUDGET_BY_ID: (id) => `${API_BASE}/budgets/${id}`,
  BUDGETS_MONTH:    `${API_BASE}/budgets/month`,
  ANALYTICS:        `${API_BASE}/budgets/analytics`,
};

// =============================================
//  Auth Helpers
// =============================================
function getToken()    { return localStorage.getItem('token'); }
function getUser()     { const u = localStorage.getItem('user'); return u ? JSON.parse(u) : null; }
function isLoggedIn()  { return !!getToken(); }

function saveAuth(data) {
  localStorage.setItem('token', data.token);
  localStorage.setItem('user', JSON.stringify({
    id: data.id, name: data.name,
    email: data.email, currency: data.currency,
    monthlyBudget: data.monthlyBudget
  }));
}

function logout() {
  localStorage.removeItem('token');
  localStorage.removeItem('user');
  window.location.href = '../index.html';
}

function requireAuth() {
  if (!isLoggedIn()) { window.location.href = '../index.html'; }
}

function getCurrencySymbol() {
  const u = getUser();
  const map = { INR:'₹', USD:'$', EUR:'€', GBP:'£', JPY:'¥' };
  return map[u?.currency] || '₹';
}

// =============================================
//  HTTP Client
// =============================================
async function http(url, options = {}) {
  const token = getToken();
  const headers = { 'Content-Type': 'application/json', ...options.headers };
  if (token) headers['Authorization'] = `Bearer ${token}`;

  const res = await fetch(url, { ...options, headers });
  const json = await res.json();

  if (!res.ok) {
    throw new Error(json.message || 'Request failed');
  }
  return json;
}

const get    = (url)       => http(url, { method: 'GET' });
const post   = (url, body) => http(url, { method: 'POST',   body: JSON.stringify(body) });
const put    = (url, body) => http(url, { method: 'PUT',    body: JSON.stringify(body) });
const del    = (url)       => http(url, { method: 'DELETE' });

// =============================================
//  Toast Notification
// =============================================
function showToast(message, type = 'success') {
  const existing = document.querySelector('.toast');
  if (existing) existing.remove();

  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;
  toast.innerHTML = `
    <span class="toast-icon">${type === 'success' ? '✓' : type === 'error' ? '✕' : 'ℹ'}</span>
    <span>${message}</span>`;
  document.body.appendChild(toast);

  setTimeout(() => toast.classList.add('show'), 10);
  setTimeout(() => { toast.classList.remove('show'); setTimeout(() => toast.remove(), 300); }, 3500);
}

// =============================================
//  Format Helpers
// =============================================
function formatCurrency(amount) {
  const sym = getCurrencySymbol();
  return `${sym}${Number(amount || 0).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
}

function formatDate(dateStr) {
  if (!dateStr) return '';
  const d = new Date(dateStr);
  return d.toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' });
}

function getCurrentMonthYear() {
  const now = new Date();
  return { month: now.getMonth() + 1, year: now.getFullYear() };
}

function getMonthName(m) {
  return ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'][m-1];
}

const CATEGORY_ICONS = {
  'Food & Dining': '🍽️', 'Transportation': '🚗', 'Shopping': '🛍️',
  'Entertainment': '🎬', 'Bills & Utilities': '💡', 'Healthcare': '🏥',
  'Education': '📚', 'Travel': '✈️', 'Personal Care': '💄',
  'Housing': '🏠', 'Salary': '💼', 'Freelance': '💻',
  'Investment': '📈', 'Other': '📦'
};

const CATEGORY_COLORS = [
  '#6C63FF','#FF6584','#43B89C','#F7B731','#FC5C65',
  '#45AAF2','#A55EEA','#26de81','#fd9644','#2bcbba'
];
