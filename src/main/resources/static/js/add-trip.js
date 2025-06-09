document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('tripForm');
    const photoInput = document.getElementById('photos');
    const imagePreview = document.getElementById('imagePreview');
    let tripId = document.getElementById('tripId')?.value;
    const isEditMode = tripId && tripId.trim() !== '';
    //const isOnEditPage = window.location.pathname.includes('/trips/edit');

    let filesToUpload = [];

    // Preview новых фото
    photoInput.addEventListener('change', function() {
        const newFiles = Array.from(this.files);
        newFiles.forEach(file => {
            filesToUpload.push(file);
            const reader = new FileReader();
            reader.onload = function(e) {
                const img = document.createElement('img');
                img.src = e.target.result;
                img.classList.add('image-preview');
                imagePreview.appendChild(img);
            };
            reader.readAsDataURL(file);
        });
        this.value = '';
    });

    // Удаление существующих фото
    // Удаление существующих фото
    document.addEventListener('click', async function(e) {
        if (e.target.closest('.delete-photo-btn')) {
            e.preventDefault();
            e.stopPropagation();

            const btn = e.target.closest('.delete-photo-btn');
            const photoId = btn.getAttribute('data-photo-id');

            if (confirm('Delete this photo?')) {
                try {
                    const response = await fetch(`/api/photos/${photoId}`, {
                        method: 'DELETE'
                    });

                    if (response.ok) {
                        btn.closest('.existing-photo').remove();
                    } else {
                        alert('Failed to delete photo');
                    }
                } catch (error) {
                    console.error('Error deleting photo:', error);
                    alert('Error deleting photo');
                }
            }
        }
    });

    // Отправка формы
    form.addEventListener('submit', async function(e) {
        e.preventDefault();

        const tripData = {
            name: form.tripName.value,
            destination: form.destination.value,
            startDate: form.startDate.value,
            endDate: form.endDate.value,
            description: form.description.value
        };

        try {
            // Для редактирования
            if (isEditMode) {
                const response = await fetch(`/api/trips/update_trip/${tripId}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(tripData)
                });
                if (!response.ok) throw new Error('Failed to update trip');
            }
            // Для создания
            else {
                const response = await fetch('/api/trips', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(tripData)
                });
                if (!response.ok) throw new Error('Failed to create trip');
                const trip = await response.json();
                tripId = trip.id; // Для загрузки фото
            }

            // Загрузка новых фото (если есть)
            if (filesToUpload.length > 0) {
                const formData = new FormData();
                formData.append('tripId', tripId);
                filesToUpload.forEach(file => formData.append('photos', file));

                const photoResponse = await fetch('/api/photos/upload', {
                    method: 'POST',
                    body: formData
                });
                if (!photoResponse.ok) throw new Error('Failed to upload photos');
            }

            window.location.href = '/home';
        } catch (error) {
            console.error('Error:', error);
            alert(`Error: ${error.message}`);
        }
    });

    // Кнопка удаления поездки (только в режиме редактирования)
    document.getElementById('deleteTripBtn')?.addEventListener('click', async function() {
        if (confirm('Are you sure you want to delete this trip? This action cannot be undone!')) {
            try {
                const response = await fetch(`/api/trips/delete_trip/${tripId}`, {
                    method: 'DELETE'
                });

                if (response.ok) {
                    window.location.href = '/home';
                } else {
                    const error = await response.text();
                    throw new Error(error || 'Failed to delete trip');
                }
            } catch (error) {
                console.error('Error deleting trip:', error);
                alert('Error deleting trip: ' + error.message);
            }
        }
    });

    // Кнопка отмены
    document.getElementById('cancelBtn').addEventListener('click', () => {
        window.location.href = '/home';
    });
});