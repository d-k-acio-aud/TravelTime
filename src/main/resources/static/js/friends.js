document.addEventListener('DOMContentLoaded', function() {
    const friendsList = document.getElementById('friendsList');
    const pendingList = document.getElementById('pendingList');
    const searchInput = document.getElementById('searchInput');
    const friendTemplate = document.getElementById('friendTemplate').content;

    // Load initial data
    loadFriends();
    loadPendingRequests();

    // Search functionality
    searchInput.addEventListener('input', function(e) {
        debounce(() => searchUsers(e.target.value), 300)();
    });

    async function loadFriends() {
        try {
            const response = await fetch('/api/friends');
            if (!response.ok) throw new Error('Failed to load friends');
            const friends = await response.json();
            renderFriends(friends, friendsList, false);
        } catch (error) {
            showAlert('Error loading friends', 'danger');
        }
    }

    async function loadPendingRequests() {
        try {
            const response = await fetch('/api/friends/pending');
            if (!response.ok) throw new Error('Failed to load pending requests');
            const requests = await response.json();
            renderPendingRequests(requests);
        } catch (error) {
            showAlert('Error loading pending requests', 'danger');
        }
    }

    function renderFriends(users, container, isSearchResult = false) {
        container.innerHTML = '';
        users.forEach(user => {
            const clone = document.importNode(friendTemplate, true);
            clone.querySelector('.friend-name').textContent = user.name || user.username;
            clone.querySelector('.friend-username').textContent = `@${user.username}`;

            const actions = clone.querySelector('.friend-actions');
            if (isSearchResult) {
                const addBtn = createButton('Add Friend', 'primary', () => sendFriendRequest(user.username));
                actions.appendChild(addBtn);
            } else {
                const removeBtn = createButton('Remove', 'danger', () => removeFriend(user.id));
                actions.appendChild(removeBtn);
            }

            container.appendChild(clone);
        });
    }

    function renderPendingRequests(requests) {
        pendingList.innerHTML = '';
        requests.forEach(request => {
            const clone = document.importNode(friendTemplate, true);
            clone.querySelector('.friend-name').textContent = request.requester.name || request.requester.username;
            clone.querySelector('.friend-username').textContent = `@${request.requester.username}`;

            const actions = clone.querySelector('.friend-actions');
            const acceptBtn = createButton('Accept', 'success', () => acceptRequest(request.id));
            const declineBtn = createButton('Decline', 'danger', () => declineRequest(request.id));
            actions.append(acceptBtn, declineBtn);

            pendingList.appendChild(clone);
        });
    }

    async function searchUsers(query) {
        try {
            // Кодируем поисковый запрос
            const encodedQuery = encodeURIComponent(query);
            const response = await fetch(`/api/users?search=${encodedQuery}`);

            if (!response.ok) throw new Error('Search failed');
            const results = await response.json();
            renderFriends(results, friendsList, true);
        } catch (error) {
            showAlert('Error searching users', 'danger');
        }
    }

    async function sendFriendRequest(username) {
        try {
            const response = await fetch(`/api/friends/request/${username}`, { method: 'POST' });
            if (!response.ok) throw new Error('Request failed');
            showAlert('Friend request sent!', 'success');
        } catch (error) {
            showAlert(error.message, 'danger');
        }
    }

    async function acceptRequest(friendshipId) {
        try {
            const response = await fetch(`/api/friends/accept/${friendshipId}`, { method: 'POST' });
            if (!response.ok) throw new Error('Accept failed');
            loadFriends();
            loadPendingRequests();
            showAlert('Request accepted!', 'success');
        } catch (error) {
            showAlert(error.message, 'danger');
        }
    }

    // Helper functions
    function createButton(text, style, onClick) {
        const btn = document.createElement('button');
        btn.className = `btn btn-${style} btn-sm`;
        btn.textContent = text;
        btn.addEventListener('click', onClick);
        return btn;
    }

    function debounce(func, timeout = 300) {
        let timer;
        return (...args) => {
            clearTimeout(timer);
            timer = setTimeout(() => { func.apply(this, args); }, timeout);
        };
    }

    function showAlert(message, type) {
        const alert = document.createElement('div');
        alert.className = `alert alert-${type} position-fixed top-0 end-0 m-3`;
        alert.textContent = message;
        document.body.appendChild(alert);
        setTimeout(() => alert.remove(), 3000);
    }
});