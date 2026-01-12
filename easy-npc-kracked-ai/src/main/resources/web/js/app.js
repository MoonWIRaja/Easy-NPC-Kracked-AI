// API Base URL
const API_BASE = '/api';

// State
let authToken = localStorage.getItem('authToken');
let currentUser = null;
let currentNPC = null;
let config = null;
let providers = [];
let currentProvider = null;

// DOM Elements
const loginScreen = document.getElementById('login-screen');
const dashboardScreen = document.getElementById('dashboard-screen');
const loginForm = document.getElementById('login-form');
const loginError = document.getElementById('login-error');

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    initEventListeners();
    if (authToken) {
        showDashboard();
    }
});

// Event Listeners
function initEventListeners() {
    // Login form
    loginForm.addEventListener('submit', handleLogin);

    // Navigation (Sidebar)
    document.querySelectorAll('.sidebar-link').forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const tab = e.currentTarget.dataset.tab;
            if (tab) switchTab(tab);
        });
    });

    // Logout
    document.getElementById('logout-btn').addEventListener('click', handleLogout);

    // NPCs
    document.getElementById('refresh-npcs-btn').addEventListener('click', loadNPCs);

    // Config
    document.getElementById('save-config-btn').addEventListener('click', saveConfig);

    // Users
    document.getElementById('add-user-btn').addEventListener('click', () => showModal('user-modal'));
    document.getElementById('create-user-btn').addEventListener('click', createUser);

    // Providers
    document.getElementById('add-provider-btn').addEventListener('click', () => openProviderModal());
    document.getElementById('save-provider-btn').addEventListener('click', saveProvider);

    // Provider chips (quick add)
    document.querySelectorAll('.provider-chip').forEach(chip => {
        chip.addEventListener('click', (e) => {
            const preset = e.currentTarget.dataset.preset;
            applyProviderPreset(preset);
        });
    });

    // Modals
    document.querySelectorAll('.modal-close, .modal-close-icon, .modal-layer').forEach(btn => {
        btn.addEventListener('click', () => {
            const modal = btn.closest('.modal');
            if (modal) modal.classList.add('hidden');
        });
    });

    // NPC Modal
    document.getElementById('save-npc-btn').addEventListener('click', saveNPC);

    // Trait sliders and generic range displays
    document.querySelectorAll('input[type="range"]').forEach(slider => {
        slider.addEventListener('input', (e) => {
            const display = e.target.nextElementSibling;
            if (display && display.classList.contains('node-val')) {
                display.textContent = e.target.value;
            } else if (display && display.classList.contains('value-display')) {
                display.textContent = e.target.value;
            } else {
                // Fallback to ID-based for NPC modal traits if needed
                const valDisplay = document.getElementById(e.target.id + '-value');
                if (valDisplay) valDisplay.textContent = e.target.value;
            }
        });
    });
}

// API Helper
async function apiCall(endpoint, options = {}) {
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers
    };

    if (authToken) {
        headers['Authorization'] = `Bearer ${authToken}`;
    }

    const response = await fetch(API_BASE + endpoint, {
        ...options,
        headers
    });

    if (response.status === 401) {
        logout();
        throw new Error('Unauthorized');
    }

    return response;
}

// Login
async function handleLogin(e) {
    e.preventDefault();
    loginError.textContent = '';

    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    try {
        const response = await apiCall('/auth/login', {
            method: 'POST',
            body: JSON.stringify({ username, password })
        });

        const data = await response.json();

        if (response.ok) {
            authToken = data.token;
            localStorage.setItem('authToken', authToken);
            showDashboard();
        } else {
            loginError.textContent = data.error || 'Login failed';
        }
    } catch (error) {
        loginError.textContent = 'Connection error. Is the server running?';
    }
}

// Logout
function handleLogout() {
    logout();
}

function logout() {
    authToken = null;
    localStorage.removeItem('authToken');
    currentUser = null;
    loginScreen.classList.remove('hidden');
    dashboardScreen.classList.add('hidden');
}

// Show Dashboard
async function showDashboard() {
    try {
        const response = await apiCall('/auth/me');
        if (response.ok) {
            currentUser = await response.json();
            document.getElementById('current-username').textContent = currentUser.username;

            // Set role tag
            const roleTag = document.querySelector('.role-tag');
            if (roleTag) roleTag.textContent = currentUser.role === 'ADMIN' ? 'System Architect' : 'Operator';

            // Show users link only for admin
            document.getElementById('users-link').style.display =
                currentUser.role === 'ADMIN' ? 'flex' : 'none';

            loginScreen.classList.add('hidden');
            dashboardScreen.classList.remove('hidden');

            // Load initial data
            loadConfig();
            loadProviders();
            loadNPCs();

            if (currentUser.role === 'ADMIN') {
                loadUsers();
            }
        } else {
            logout();
        }
    } catch (error) {
        logout();
    }
}

// Switch Tabs
function switchTab(tabName) {
    document.querySelectorAll('.tab').forEach(tab => tab.classList.remove('active'));
    document.querySelectorAll('.sidebar-link').forEach(link => link.classList.remove('active'));

    document.getElementById(tabName + '-tab').classList.add('active');
    const activeLink = document.querySelector(`.sidebar-link[data-tab="${tabName}"]`);
    if (activeLink) activeLink.classList.add('active');

    if (tabName === 'npcs') loadNPCs();
    if (tabName === 'settings') {
        loadConfig();
        loadProviders();
    }
    if (tabName === 'users' && currentUser.role === 'ADMIN') loadUsers();
}

// Load NPCs
async function loadNPCs() {
    try {
        const response = await apiCall('/npcs');
        const npcs = await response.json();

        const npcsList = document.getElementById('npcs-list');
        const noNPCs = document.getElementById('no-npcs');

        if (npcs.length === 0) {
            npcsList.innerHTML = '';
            noNPCs.classList.remove('hidden');
            return;
        }

        noNPCs.classList.add('hidden');
        npcsList.innerHTML = npcs.map(npc => createNPCCard(npc)).join('');
    } catch (error) {
        console.error('Failed to load NPCs:', error);
        showToast('Failed to load NPCs', 'error');
    }
}

// Create NPC Card
function createNPCCard(npc) {
    const statusClass = npc.status ? npc.status.toLowerCase() : 'idle';
    const aiEnabled = npc.aiEnabled ? 'Matrix Active' : 'Matrix Offline';
    const traits = npc.personalityTraits || {};

    return `
        <div class="npc-card" data-uuid="${npc.entityUuid}">
            <div class="npc-card-header">
                <div class="npc-name">${escapeHtml(npc.entityName)}</div>
                <div class="npc-status ${statusClass}">
                    <div class="status-dot"></div>
                    ${npc.status || 'Idle'}
                </div>
            </div>
            <div class="npc-info">
                <p>Type: ${escapeHtml(npc.entityType || 'Unknown')}</p>
                <p>State: ${aiEnabled}</p>
                ${npc.personality ? `<p class="personality-quote">"${escapeHtml(npc.personality)}"</p>` : ''}
            </div>
            <div class="npc-traits">
                <span class="trait-badge">Soc: ${(traits.friendliness || 0).toFixed(1)}</span>
                <span class="trait-badge">Cur: ${(traits.curiosity || 0).toFixed(1)}</span>
                <span class="trait-badge">Wit: ${(traits.humor || 0).toFixed(1)}</span>
            </div>
        </div>
    `;
}

// NPC Card Click
document.addEventListener('click', (e) => {
    const card = e.target.closest('.npc-card');
    if (card) {
        const uuid = card.dataset.uuid;
        openNPCModal(uuid);
    }
});

// Open NPC Modal
async function openNPCModal(uuid) {
    try {
        const response = await apiCall(`/npcs/${uuid}`);
        const npc = await response.json();

        currentNPC = npc;

        // Update provider dropdown first
        updateNPCProviderDropdown();

        document.getElementById('modal-npc-name').textContent = npc.entityName;
        document.getElementById('npc-ai-enabled').checked = npc.aiEnabled || false;
        document.getElementById('npc-voice-enabled').checked = npc.voiceEnabled || false;
        document.getElementById('npc-provider').value = npc.aiProviderId || 'DEFAULT';
        document.getElementById('npc-personality').value = npc.personality || '';
        document.getElementById('npc-system-prompt').value = npc.systemPrompt || '';

        // Load traits
        const traits = npc.personalityTraits || {};
        const setTrait = (id, val) => {
            const slider = document.getElementById(id);
            const display = document.getElementById(id + '-value');
            if (slider) slider.value = val;
            if (display) display.textContent = val.toFixed(1);
        };

        setTrait('trait-friendliness', traits.friendliness || 0.7);
        setTrait('trait-curiosity', traits.curiosity || 0.5);
        setTrait('trait-humor', traits.humor || 0.3);
        setTrait('trait-aggression', traits.aggression || 0.1);

        showModal('npc-modal');
    } catch (error) {
        console.error('Failed to load NPC:', error);
        showToast('Failed to load NPC details', 'error');
    }
}

// Save NPC
async function saveNPC() {
    if (!currentNPC) return;

    const personalityTraits = {
        friendliness: parseFloat(document.getElementById('trait-friendliness').value),
        curiosity: parseFloat(document.getElementById('trait-curiosity').value),
        humor: parseFloat(document.getElementById('trait-humor').value),
        aggression: parseFloat(document.getElementById('trait-aggression').value)
    };

    const data = {
        systemPrompt: document.getElementById('npc-system-prompt').value,
        personality: document.getElementById('npc-personality').value,
        aiProviderId: document.getElementById('npc-provider').value,
        aiEnabled: document.getElementById('npc-ai-enabled').checked,
        voiceEnabled: document.getElementById('npc-voice-enabled').checked,
        personalityTraits
    };

    try {
        const response = await apiCall(`/npcs/${currentNPC.entityUuid}`, {
            method: 'PUT',
            body: JSON.stringify(data)
        });

        if (response.ok) {
            showToast('Matrix data updated successfully', 'success');
            hideModal('npc-modal');
            loadNPCs();
        } else {
            const error = await response.json();
            showToast(error.error || 'Update failed', 'error');
        }
    } catch (error) {
        console.error('Failed to save NPC:', error);
        showToast('Matrix synchronization error', 'error');
    }
}

// Load Config
async function loadConfig() {
    try {
        const response = await apiCall('/config');
        config = await response.json();

        // NPC config
        document.getElementById('ai-think-interval').value = config.npc.aiThinkIntervalTicks;
        document.querySelector('#ai-think-interval + .value-display').textContent = config.npc.aiThinkIntervalTicks;

        document.getElementById('personality-rate').value = config.npc.personalityEvolutionRate;
        document.querySelector('#personality-rate + .value-display').textContent = config.npc.personalityEvolutionRate;

        // Voice config
        document.getElementById('voice-enabled').checked = config.voice.enabled;
        document.getElementById('tts-provider').value = config.voice.ttsProvider;

        // Web server config
        document.getElementById('web-enabled').checked = config.webServer.enabled;
        document.getElementById('web-port').value = config.webServer.port;
    } catch (error) {
        console.error('Failed to load config:', error);
    }
}

// Save Config
async function saveConfig() {
    const data = {
        ai: {
            defaultProviderId: document.getElementById('default-provider').value || null
        },
        npc: {
            aiThinkIntervalTicks: parseInt(document.getElementById('ai-think-interval').value),
            personalityEvolutionRate: parseFloat(document.getElementById('personality-rate').value)
        },
        voice: {
            enabled: document.getElementById('voice-enabled').checked,
            ttsProvider: document.getElementById('tts-provider').value
        },
        webServer: {
            enabled: document.getElementById('web-enabled').checked,
            port: parseInt(document.getElementById('web-port').value)
        }
    };

    try {
        const response = await apiCall('/config', {
            method: 'PUT',
            body: JSON.stringify(data)
        });

        if (response.ok) {
            showToast('Core logic committed successfully', 'success');
        } else {
            const error = await response.json();
            showToast(error.error || 'Commit failed', 'error');
        }
    } catch (error) {
        console.error('Failed to save config:', error);
        showToast('Transmission error', 'error');
    }
}

// Load Providers
async function loadProviders() {
    try {
        const response = await apiCall('/config/providers');
        providers = await response.json();

        const providersList = document.getElementById('providers-list');
        const noProviders = document.getElementById('no-providers');
        const defaultProviderSelect = document.getElementById('default-provider');

        if (providers.length === 0) {
            providersList.innerHTML = '';
            noProviders.classList.remove('hidden');
            defaultProviderSelect.innerHTML = '<option value="">No providers configured</option>';
            return;
        }

        noProviders.classList.add('hidden');
        providersList.innerHTML = providers.map(provider => createProviderCard(provider)).join('');

        // Populate default provider dropdown
        defaultProviderSelect.innerHTML = providers.map(p =>
            `<option value="${p.id}">${escapeHtml(p.name)}</option>`
        ).join('');

        // Set current default
        if (config?.ai?.defaultProviderId) {
            defaultProviderSelect.value = config.ai.defaultProviderId;
        }

        // Also update NPC modal provider dropdown
        updateNPCProviderDropdown();
    } catch (error) {
        console.error('Failed to load providers:', error);
        showToast('Failed to load AI providers', 'error');
    }
}

// Create Provider Card
function createProviderCard(provider) {
    const isDefault = config?.ai?.defaultProviderId === provider.id;
    return `
        <div class="provider-card" data-id="${provider.id}">
            <div class="provider-card-header">
                <span class="provider-name">${escapeHtml(provider.name)}</span>
                ${isDefault ? '<span class="default-badge">Default</span>' : ''}
            </div>
            <div class="provider-info">
                <p>Endpoint: ${escapeHtml(truncateUrl(provider.endpoint || 'Not set'))}</p>
                <p>Model: ${escapeHtml(provider.model || 'Not set')}</p>
            </div>
            <div class="provider-actions">
                <button class="action-btn edit-btn" onclick="editProvider('${provider.id}')">Edit</button>
                ${!isDefault ? `<button class="action-btn delete-btn" onclick="deleteProvider('${provider.id}')">Delete</button>` : ''}
            </div>
        </div>
    `;
}

// Truncate URL for display
function truncateUrl(url) {
    if (url.length > 40) {
        return url.substring(0, 37) + '...';
    }
    return url;
}

// Open Provider Modal (Add new)
function openProviderModal(providerId = null) {
    currentProvider = null;

    document.getElementById('provider-id').value = '';
    document.getElementById('provider-name').value = '';
    document.getElementById('provider-apikey').value = '';
    document.getElementById('provider-endpoint').value = '';
    document.getElementById('provider-model').value = '';

    document.getElementById('provider-modal-title').textContent = 'Add Neural Nexus Provider';
    showModal('provider-modal');
}

// Edit Provider
async function editProvider(providerId) {
    try {
        const provider = providers.find(p => p.id === providerId);
        if (!provider) {
            showToast('Provider not found', 'error');
            return;
        }

        currentProvider = provider;

        document.getElementById('provider-id').value = provider.id;
        document.getElementById('provider-name').value = provider.name || '';
        document.getElementById('provider-apikey').value = provider.apiKey || '';
        document.getElementById('provider-endpoint').value = provider.endpoint || '';
        document.getElementById('provider-model').value = provider.model || '';

        document.getElementById('provider-modal-title').textContent = 'Edit Neural Nexus Provider';
        showModal('provider-modal');
    } catch (error) {
        console.error('Failed to load provider:', error);
        showToast('Failed to load provider details', 'error');
    }
}

// Save Provider
async function saveProvider() {
    const data = {
        name: document.getElementById('provider-name').value,
        apiKey: document.getElementById('provider-apikey').value || null,
        endpoint: document.getElementById('provider-endpoint').value,
        model: document.getElementById('provider-model').value
    };

    if (!data.name || !data.endpoint || !data.model) {
        showToast('Name, Endpoint, and Model are required', 'error');
        return;
    }

    try {
        let response;
        if (currentProvider) {
            // Update existing
            response = await apiCall(`/config/providers/${currentProvider.id}`, {
                method: 'PUT',
                body: JSON.stringify(data)
            });
        } else {
            // Add new
            response = await apiCall('/config/providers', {
                method: 'POST',
                body: JSON.stringify(data)
            });
        }

        if (response.ok) {
            showToast('Neural Nexus Provider configured successfully', 'success');
            hideModal('provider-modal');
            loadProviders();
        } else {
            const error = await response.json();
            showToast(error.error || 'Configuration failed', 'error');
        }
    } catch (error) {
        console.error('Failed to save provider:', error);
        showToast('Matrix transmission error', 'error');
    }
}

// Delete Provider
async function deleteProvider(providerId) {
    if (!confirm('Are you sure you want to disconnect this Neural Nexus Provider?')) return;

    try {
        const response = await apiCall(`/config/providers/${providerId}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            showToast('Neural Nexus Provider disconnected', 'success');
            loadProviders();
        } else {
            showToast('Disconnection failed', 'error');
        }
    } catch (error) {
        console.error('Failed to delete provider:', error);
        showToast('System execution error', 'error');
    }
}

// Apply Provider Preset
function applyProviderPreset(preset) {
    const presets = {
        openai: {
            name: 'OpenAI',
            endpoint: 'https://api.openai.com/v1',
            model: 'gpt-4o'
        },
        deepseek: {
            name: 'DeepSeek',
            endpoint: 'https://api.deepseek.com/v1',
            model: 'deepseek-chat'
        },
        grok: {
            name: 'Grok',
            endpoint: 'https://api.x.ai/v1',
            model: 'grok-beta'
        },
        ollama: {
            name: 'Ollama',
            endpoint: 'http://localhost:11434/v1',
            model: 'llama3.2'
        },
        anthropic: {
            name: 'Anthropic',
            endpoint: 'https://api.anthropic.com/v1',
            model: 'claude-sonnet-4-20250514'
        }
    };

    const config = presets[preset];
    if (config) {
        document.getElementById('provider-name').value = config.name;
        document.getElementById('provider-endpoint').value = config.endpoint;
        document.getElementById('provider-model').value = config.model;
    }
}

// Update NPC Modal Provider Dropdown
function updateNPCProviderDropdown() {
    const select = document.getElementById('npc-provider');
    if (!select) return;

    select.innerHTML = '<option value="DEFAULT">Default Provider</option>' +
        providers.map(p => `<option value="${p.id}">${escapeHtml(p.name)}</option>`).join('');
}

// Test AI Connection
async function testAI(provider) {
    showToast('Pinging neural source...', 'info');

    try {
        const response = await apiCall('/ai/test', {
            method: 'POST',
            body: JSON.stringify({ provider })
        });

        const result = await response.json();

        if (result.success) {
            showToast(`Nexus Link Established: ${result.provider}`, 'success');
        } else {
            showToast(result.error || 'Nexus Unreachable', 'error');
        }
    } catch (error) {
        console.error('AI test failed:', error);
        showToast('Nexus Protocol Error', 'error');
    }
}

// Load Users
async function loadUsers() {
    try {
        const response = await apiCall('/users');
        const users = await response.json();

        const usersList = document.getElementById('users-list');
        const noUsers = document.getElementById('no-users');

        if (users.length === 0) {
            usersList.innerHTML = '';
            if (noUsers) noUsers.classList.remove('hidden');
            return;
        }

        if (noUsers) noUsers.classList.add('hidden');
        usersList.innerHTML = users.map(user => `
            <div class="user-item-card">
                <div class="user-meta">
                    <div class="avatar-small"></div>
                    <div class="user-text">
                        <span class="user-name">${escapeHtml(user.username)}</span>
                        <span class="user-role-badge ${user.role.toLowerCase()}">${user.role}</span>
                    </div>
                </div>
                ${user.id !== currentUser.id ? `
                    <button class="delete-btn" onclick="deleteUser('${user.id}')">Purge</button>
                ` : '<span class="self-tag">Current Access</span>'}
            </div>
        `).join('');
    } catch (error) {
        console.error('Failed to load users:', error);
    }
}

// Create User
async function createUser() {
    const username = document.getElementById('new-username').value;
    const password = document.getElementById('new-password').value;
    const role = document.getElementById('new-user-role').value;

    if (!username || !password) {
        showToast('Identity and Credential required', 'error');
        return;
    }

    try {
        const response = await apiCall('/users', {
            method: 'POST',
            body: JSON.stringify({ username, password, role })
        });

        if (response.ok) {
            showToast('Operator registered successfully', 'success');
            hideModal('user-modal');
            document.getElementById('new-username').value = '';
            document.getElementById('new-password').value = '';
            loadUsers();
        } else {
            const error = await response.json();
            showToast(error.error || 'Registration failed', 'error');
        }
    } catch (error) {
        console.error('Failed to create user:', error);
        showToast('Matrix transmission error', 'error');
    }
}

// Delete User
async function deleteUser(userId) {
    if (!confirm('Are you sure you want to purge this operator?')) return;

    try {
        const response = await apiCall(`/users/${userId}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            showToast('Operator purged from system', 'success');
            loadUsers();
        } else {
            showToast('Purge failed', 'error');
        }
    } catch (error) {
        console.error('Failed to delete user:', error);
        showToast('System execution error', 'error');
    }
}

// Modal Helpers
function showModal(id) {
    const modal = document.getElementById(id);
    modal.classList.remove('hidden');
    // Animate pop-in
    const content = modal.querySelector('.modal-content-wrapper');
    if (content) {
        content.style.animation = 'none';
        content.offsetHeight; // trigger reflow
        content.style.animation = null;
    }
}

function hideModal(id) {
    document.getElementById(id).classList.add('hidden');
}

// Toast Notifications
function showToast(message, type = 'info') {
    const container = document.getElementById('toast-container');
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.textContent = message;
    container.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateX(20px)';
        setTimeout(() => toast.remove(), 300);
    }, 4000);
}

// Utility: Escape HTML
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// Make globally available
window.testAI = testAI;
window.deleteUser = deleteUser;
window.editProvider = editProvider;
window.deleteProvider = deleteProvider;
