document.addEventListener('DOMContentLoaded', function () {
    const friendsList = document.getElementById('friendsList');
    //const pendingList = document.getElementById('pendingList');
    const searchInput = document.getElementById('searchInput');
    const friendTemplate = document.getElementById('friendTemplate').content;

    // Load data
    loadFriends();
    //loadPendingRequests();
    loadIncomingRequests();
    loadOutgoingRequests();

    // Debounce search
    const debouncedSearch = debounce((query) => {
        searchUsers(query);
    }, 300);

    searchInput.addEventListener('input', function (e) {
        debouncedSearch(e.target.value);
    });

    async function loadFriends() {
        try {
            const response = await fetch('/api/friends', {
                credentials: 'include'
            });
            if (!response.ok) throw new Error(await response.text());
            const friends = await response.json();
            renderFriends(friends, friendsList, false);
        } catch (error) {
            showAlert('Error loading friends: ' + error.message, 'danger');
        }
    }

    // async function loadPendingRequests() {
    //     try {
    //          const response = await fetch('/api/friends/pending', {
    //             credentials: 'include'
    //         });
    //         if (!response.ok) throw new Error(await response.text());
    //         const requests = await response.json();
    //         renderPendingRequests(requests);
    //     } catch (error) {
    //         showAlert('Error loading pending requests: ' + error.message, 'danger');
    //     }
    // }

    async function loadIncomingRequests() {
        try {
            const response = await fetch('/api/friends/requests/incoming', {
                credentials: 'include'
            });
            if (!response.ok) throw new Error(await response.text());
            const requests = await response.json();
            renderIncomingRequests(requests);
        } catch (error) {
            showAlert('Error loading incoming requests: ' + error.message, 'danger');
        }
    }

    async function loadOutgoingRequests() {
        try {
            const response = await fetch('/api/friends/requests/outgoing', {
                credentials: 'include'
            });
            if (!response.ok) throw new Error(await response.text());
            const requests = await response.json();
            renderOutgoingRequests(requests);
        } catch (error) {
            showAlert('Error loading outgoing requests: ' + error.message, 'danger');
        }
    }

    function renderIncomingRequests(requests) {
        const container = document.getElementById('incomingList');
        container.innerHTML = '';

        if (requests.length === 0) {
            container.innerHTML = '<p class="text-muted">You have no incoming requests</p>';
            return;
        }

        requests.forEach(request => {
            const clone = document.importNode(friendTemplate, true);
            clone.querySelector('.friend-name').textContent = request.username;
            clone.querySelector('.friend-username').textContent = `@${request.username}`;

            const actions = clone.querySelector('.friend-actions');
            const acceptBtn = createButton('Accept', 'success', () => acceptRequest(request.id));
            const declineBtn = createButton('Decline', 'danger', () => cancelRequest(request.id));
            actions.append(acceptBtn, declineBtn);

            container.appendChild(clone);
        });
    }

    function renderOutgoingRequests(requests) {
        const container = document.getElementById('outgoingList');
        container.innerHTML = '';

        if (requests.length === 0) {
            container.innerHTML = '<p class="text-muted">You have no outgoing requests</p>';
            return;
        }

        requests.forEach(request => {
            const clone = document.importNode(friendTemplate, true);
            clone.querySelector('.friend-name').textContent = request.username;
            clone.querySelector('.friend-username').textContent = `@${request.username}`;

            const actions = clone.querySelector('.friend-actions');
            const cancelBtn = createButton('Cancel Request', 'secondary', async () => {
                if (confirm('Cancel friend request?')) {
                    await cancelRequest(request.id);
                    loadOutgoingRequests();
                }
            });


            actions.append(cancelBtn);

            container.appendChild(clone);
        });
    }



    function renderFriends(friends, container) {
        container.innerHTML = '';

        if (friends.length === 0) {
            container.innerHTML = '<p class="text-muted">You have no friends</p>';
            return;
        }

        friends.forEach(friend => {
            const clone = document.importNode(friendTemplate, true);
            const nameElement = clone.querySelector('.friend-name');
            nameElement.textContent = friend.username;
            nameElement.style.cursor = 'pointer';
            nameElement.classList.add('text-primary');
            nameElement.addEventListener('click', () => {
                window.location.href = `/friend-profile/${friend.username}`;
            });

            clone.querySelector('.friend-username').textContent = `@${friend.username}`;
            const actions = clone.querySelector('.friend-actions');

            const removeBtn = createButton('Remove Friend', 'danger', async () => {
                if (confirm('Are you sure you want to remove this friend?')) {
                    await cancelRequest(friend.friendshipId);
                    await loadFriends();
                }
            });

            actions.appendChild(removeBtn);

            container.appendChild(clone);
        });
    }



    // function renderPendingRequests(requests) {
    //     pendingList.innerHTML = '';
    //     requests.forEach(request => {
    //         const clone = document.importNode(friendTemplate, true);
    //         clone.querySelector('.friend-name').textContent = request.username;
    //         clone.querySelector('.friend-username').textContent = `@${request.username}`;
    //
    //         const actions = clone.querySelector('.friend-actions');
    //         const acceptBtn = createButton('Accept', 'success', () => acceptRequest(request.id));
    //         const declineBtn = createButton('Decline', 'danger', () => cancelRequest(request.id));
    //         actions.append(acceptBtn, declineBtn);
    //
    //         pendingList.appendChild(clone);
    //     });
    // }

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
            const response = await fetch(`/api/users?search=${encodedQuery}`, {
                credentials: 'include'
            });
            if (!response.ok) throw new Error(await response.text());

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
                    const addBtn = createButton('Add Friend', 'primary', () => sendFriendRequest(user.username));
                    actions.appendChild(addBtn);

                    div.appendChild(actions);
                    resultsBox.appendChild(div);
                });
            }

            dropdown.style.display = 'block';
        } catch (error) {
            showAlert('Search error: ' + error.message, 'danger');
            dropdown.style.display = 'none';
        }
    }

    async function sendFriendRequest(username) {
        try {
            const response = await fetch(`/api/friends/request/${username}`, {
                method: 'POST',
                credentials: 'include'
            });
            if (!response.ok) throw new Error(await response.text());

            showAlert('Friend request sent!', 'success');

            // 👇 Удалим из поиска (обновим окно)
            document.getElementById('searchDropdown').style.display = 'none';
            document.getElementById('searchResults').innerHTML = '';

            // 👉 Перезагрузим pending-запросы
            loadIncomingRequests();
            loadOutgoingRequests();


        } catch (error) {
            showAlert(error.message, 'danger');
        }
    }


    async function acceptRequest(friendshipId) {
        try {
            const response = await fetch(`/api/friends/accept/${friendshipId}`, {
                method: 'POST',
                credentials: 'include'
            });
            if (!response.ok) throw new Error(await response.text());
            loadFriends();
            loadIncomingRequests();
            loadOutgoingRequests();
            showAlert('Request accepted!', 'success');
        } catch (error) {
            showAlert(error.message, 'danger');
        }
    }

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

    document.addEventListener('click', function (event) {
        const searchBox = document.querySelector('.search-box');
        if (!searchBox.contains(event.target)) {
            document.getElementById('searchDropdown').style.display = 'none';
        }
    });

    async function cancelRequest(friendshipId) {
        try {
            const response = await fetch(`/api/friends/cancel/${friendshipId}`, {
                method: 'DELETE',
                credentials: 'include'
            });
            if (!response.ok) throw new Error(await response.text());
            showAlert('Request cancelled', 'info');
            await loadIncomingRequests();
            await loadOutgoingRequests();
        } catch (error) {
            showAlert(error.message, 'danger');
        }
    }

});
