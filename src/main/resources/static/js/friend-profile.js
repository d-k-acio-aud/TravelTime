document.addEventListener('DOMContentLoaded', async () => {
    const username = window.location.pathname.split('/').pop(); // из /friend-profile/danik

    const nameEl = document.getElementById('friendName');
    const tripsContainer = document.getElementById('tripsContainer');

    try {
        const response = await fetch(`/api/users/profile/${username}`);
        if (!response.ok) throw new Error('Failed to load profile');

        const user = await response.json();

        nameEl.textContent = `${user.username}'s Trips`;

        if (!user.trips || user.trips.length === 0) {
            tripsContainer.innerHTML = '<p class="text-muted">No trips available.</p>';
        } else {
            user.trips.forEach(trip => {
                const div = document.createElement('div');
                div.className = 'card mb-3 p-3';
                div.innerHTML = `
                    <h5>${trip.title}</h5>
                    <p>${trip.description || 'No description provided.'}</p>
                `;
                tripsContainer.appendChild(div);
            });
        }
    } catch (error) {
        nameEl.textContent = 'Error loading profile';
        tripsContainer.innerHTML = `<div class="alert alert-danger">${error.message}</div>`;
    }
});
