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
            const nameElement = clone.querySelector('.friend-name');
            nameElement.textContent = user.username;
            nameElement.style.cursor = 'pointer';
            nameElement.classList.add('text-primary');
            nameElement.addEventListener('click', () => {
                window.location.href = `/friend-profile/${user.username}`;
            });


            clone.querySelector('.friend-username').textContent = `@${user.username}`;

            const actions = clone.querySelector('.friend-actions');

            if (isSearchResult) {
                if (!user.friend && !user.pending) {
                    const addBtn = createButton('Add Friend', 'primary', () => sendFriendRequest(user.username));
                    actions.appendChild(addBtn);
                } else if (user.pending) {
                    const info = document.createElement('span');
                    info.className = 'text-muted small';
                    info.textContent = 'Request pending';
                    actions.appendChild(info);
                } else if (user.friend) {
                    const removeBtn = createButton('Remove', 'danger', () => removeFriend(user.id));
                    actions.appendChild(removeBtn);
                }

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
            clone.querySelector('.friend-name').textContent = request.username;
            clone.querySelector('.friend-username').textContent = `@${request.username}`;


            const actions = clone.querySelector('.friend-actions');
            const acceptBtn = createButton('Accept', 'success', () => acceptRequest(request.id));
            const declineBtn = createButton('Decline', 'danger', () => declineRequest(request.id));
            actions.append(acceptBtn, declineBtn);

            pendingList.appendChild(clone);
        });
    }

    async function searchUsers(query) {
        const dropdown = document.getElementById('searchDropdown');
        const resultsBox = document.getElementById('searchResults');

        if (query.trim().length === 0) {
            dropdown.style.display = 'none';
            resultsBox.innerHTML = '';
            return;
        }

        try {
            const encodedQuery = encodeURIComponent(query);
            const response = await fetch(`/api/users?search=${encodedQuery}`);
            if (!response.ok) throw new Error();

            const results = await response.json();
            resultsBox.innerHTML = '';

            if (results.length === 0) {
                resultsBox.innerHTML = '<p class="text-muted px-2">No results</p>';
            } else {
                results.forEach(user => {
                    const div = document.createElement('div');
                    div.className = 'friend-card';
                    div.innerHTML = `
        <img src="https://via.placeholder.com/50" class="friend-avatar" alt="">
        <div class="friend-info">
            <h5 class="friend-name mb-1">${user.username}</h5>
            <small class="text-muted">@${user.username}</small>
        </div>
    `;

                    const actions = document.createElement('div');
                    actions.className = 'friend-actions';

                    if (!user.friend && !user.pending) {
                        const addBtn = createButton('Add Friend', 'primary', () => sendFriendRequest(user.username));
                        actions.appendChild(addBtn);
                    } else if (user.pending) {
                        const info = document.createElement('span');
                        info.className = 'text-muted small';
                        info.textContent = 'Request pending';
                        actions.appendChild(info);
                    } else if (user.friend) {
                        const removeBtn = createButton('Remove', 'danger', () => removeFriend(user.id));
                        actions.appendChild(removeBtn);
                    }

                    div.appendChild(actions);
                    resultsBox.appendChild(div);
                });

            }

            dropdown.style.display = 'block';
        } catch (error) {
            dropdown.style.display = 'none';
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
    document.addEventListener('click', function(event) {
        const searchBox = document.querySelector('.search-box');
        if (!searchBox.contains(event.target)) {
            document.getElementById('searchDropdown').style.display = 'none';
        }
    });

});