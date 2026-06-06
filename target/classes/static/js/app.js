/* ============================================
   SmartPelayanan - Shared JavaScript
   ============================================ */

const API_BASE = '/api/v1';

/**
 * Get auth token from localStorage
 */
function getToken() {
    return localStorage.getItem('token');
}

/**
 * Check if user is authenticated, redirect to login if not
 */
function requireAuth() {
    const token = getToken();
    if (!token) {
        window.location.href = '/login';
        return null;
    }
    return token;
}

/**
 * Get user data from localStorage
 */
function getUser() {
    try {
        return JSON.parse(localStorage.getItem('user'));
    } catch {
        return null;
    }
}

/**
 * Logout user
 */
function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.href = '/login';
}

/**
 * Show toast notification
 */
function showToast(message, type = 'info') {
    const container = document.getElementById('toastContainer');
    if (!container) return;

    const icons = {
        success: 'fa-check-circle',
        error: 'fa-exclamation-circle',
        warning: 'fa-exclamation-triangle',
        info: 'fa-info-circle'
    };

    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.innerHTML = `<i class="fas ${icons[type] || icons.info}"></i> ${message}`;
    container.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateX(100%)';
        toast.style.transition = 'all 0.3s ease';
        setTimeout(() => toast.remove(), 300);
    }, 4000);
}

/**
 * Format date string to localized format
 */
function formatDate(dateStr) {
    if (!dateStr) return '-';
    try {
        const d = new Date(dateStr);
        return d.toLocaleDateString('id-ID', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    } catch {
        return dateStr;
    }
}

/**
 * Format date short (for tables)
 */
function formatDateShort(dateStr) {
    if (!dateStr) return '-';
    try {
        const d = new Date(dateStr);
        return d.toLocaleDateString('id-ID', {
            year: 'numeric',
            month: 'short',
            day: 'numeric'
        });
    } catch {
        return dateStr;
    }
}

/**
 * Show/hide element
 */
function show(id) {
    const el = document.getElementById(id);
    if (el) el.style.display = 'block';
}

function hide(id) {
    const el = document.getElementById(id);
    if (el) el.style.display = 'none';
}

/**
 * Show loading state on a button
 */
function setLoading(btn, loading) {
    if (loading) {
        btn._origHtml = btn.innerHTML;
        btn.disabled = true;
        btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Loading...';
    } else {
        btn.disabled = false;
        if (btn._origHtml) btn.innerHTML = btn._origHtml;
    }
}

/**
 * Safe API fetch with auth header
 */
async function apiFetch(url, options = {}) {
    const token = getToken();
    const headers = {
        'Content-Type': 'application/json',
        ...(token ? { 'Authorization': 'Bearer ' + token } : {}),
        ...options.headers
    };

    const res = await fetch(url, { ...options, headers });

    if (res.status === 401) {
        logout();
        return null;
    }

    return res.json();
}

/**
 * Modal helpers
 */
function openModal(id) {
    const modal = document.getElementById(id);
    if (modal) modal.classList.add('active');
}

function closeModal(id) {
    const modal = document.getElementById(id);
    if (modal) modal.classList.remove('active');
}

// Close modal on overlay click and ESC key
document.addEventListener('click', function (e) {
    if (e.target.classList.contains('modal')) {
        e.target.classList.remove('active');
    }
});

document.addEventListener('keydown', function (e) {
    if (e.key === 'Escape') {
        document.querySelectorAll('.modal.active').forEach(m => m.classList.remove('active'));
    }
});

/**
 * Sidebar toggle for mobile
 */
function toggleSidebar() {
    const sidebar = document.querySelector('.sidebar');
    const overlay = document.querySelector('.sidebar-overlay');
    if (sidebar) sidebar.classList.toggle('open');
    if (overlay) overlay.classList.toggle('active');
}

// Close sidebar on overlay click
document.addEventListener('click', function (e) {
    if (e.target.classList.contains('sidebar-overlay')) {
        document.querySelector('.sidebar')?.classList.remove('open');
        e.target.classList.remove('active');
    }
});

/**
 * File size formatter
 */
function formatFileSize(bytes) {
    if (!bytes) return '';
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(1024));
    return (bytes / Math.pow(1024, i)).toFixed(1) + ' ' + sizes[i];
}

/**
 * Initialize sidebar active state based on current page
 */
function initSidebar() {
    const currentPath = window.location.pathname;
    document.querySelectorAll('.menu-item').forEach(item => {
        const href = item.getAttribute('href');
        if (href && currentPath.startsWith(href)) {
            item.classList.add('active');
        }
    });
}

// Run on DOM ready
document.addEventListener('DOMContentLoaded', function () {
    initSidebar();

    // Add toast container if not exists
    if (!document.getElementById('toastContainer')) {
        const container = document.createElement('div');
        container.id = 'toastContainer';
        container.className = 'toast-container';
        document.body.appendChild(container);
    }

    // Add sidebar overlay if not exists
    if (!document.querySelector('.sidebar-overlay')) {
        const overlay = document.createElement('div');
        overlay.className = 'sidebar-overlay';
        document.body.appendChild(overlay);
    }
});
