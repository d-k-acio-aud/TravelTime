document.addEventListener('DOMContentLoaded', async () => {
    const container = document.getElementById('photoGallery');

    try {
        const response = await fetch('/api/photos');
        if (!response.ok) throw new Error('Ошибка при загрузке фото');

        const photos = await response.json();

        if (photos.length === 0) {
            container.innerHTML = '<p>Пока нет загруженных фотографий.</p>';
            return;
        }

        photos.forEach(photo => {
            const a = document.createElement('a');
            a.href = photo.url;
            a.classList.add('glightbox');
            a.setAttribute('data-gallery', 'all-photos');

            const img = document.createElement('img');
            img.src = photo.url;
            img.classList.add('image-preview');

            a.appendChild(img);
            container.appendChild(a);
        });

        GLightbox({ selector: '.glightbox' });

    } catch (error) {
        console.error('Ошибка загрузки фото:', error);
        container.innerHTML = '<p>Не удалось загрузить фотографии.</p>';
    }
});
