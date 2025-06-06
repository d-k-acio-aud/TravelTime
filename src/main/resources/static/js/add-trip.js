document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('tripForm');
    const photoInput = document.getElementById('photos');
    const imagePreview = document.getElementById('imagePreview');
    let filesToUpload = [];

    // Preview images
    photoInput.addEventListener('change', function () {
        const newFiles = Array.from(this.files);

        newFiles.forEach(file => {
            filesToUpload.push(file);  // добавляем, а не заменяем

            const reader = new FileReader();
            reader.onload = function (e) {
                const img = document.createElement('img');
                img.src = e.target.result;
                img.classList.add('image-preview');
                imagePreview.appendChild(img);
            };
            reader.readAsDataURL(file);
        });

        // Сброс input, чтобы можно было выбрать тот же файл повторно
        this.value = '';
    });


    // Form submission
    form.addEventListener('submit', async function (e) {
        e.preventDefault();

        const tripData = {
            name: form.tripName.value,
            destination: form.destination.value,
            startDate: form.startDate.value,
            endDate: form.endDate.value,
            description: form.description.value // <--- добавили описание
        };

        try {
            // Create trip
            const tripResponse = await fetch('/api/trips', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(tripData)
            });

            if (!tripResponse.ok) throw new Error('Trip creation failed');

            const trip = await tripResponse.json();

            // Upload photos
            if (filesToUpload.length > 0) {
                const formData = new FormData();
                formData.append('tripId', trip.id);
                filesToUpload.forEach(file => {
                    formData.append('photos', file);
                });

                const photoResponse = await fetch('/api/photos/upload', {
                    method: 'POST',
                    body: formData
                });

                if (!photoResponse.ok) throw new Error('Photo upload failed');
            }

            window.location.href = '/home';

        } catch (error) {
            console.error('Error:', error);
            alert(`Error: ${error.message}`);
        }
    });

    // Cancel button
    document.getElementById('cancelBtn').addEventListener('click', () => {
        window.location.href = '/home';
    });
});
