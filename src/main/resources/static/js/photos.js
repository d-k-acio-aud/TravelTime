document.addEventListener('DOMContentLoaded', async () => {
    const gallery = document.getElementById('photoGallery');
    const noPhotosMsg = document.getElementById('noPhotosMessage');

    try {
        const response = await fetch('/api/photos');
        if (!response.ok) throw new Error('Failed to load photos');

        const photos = await response.json();

        if (photos.length === 0) {
            noPhotosMsg.style.display = 'block';
            return;
        }

        noPhotosMsg.style.display = 'none';
        gallery.innerHTML = '';

        photos.forEach(photo => {
            const card = document.createElement('div');
            card.classList.add('image-card');

            const link = document.createElement('a');
            link.href = photo.url;
            link.classList.add('glightbox');
            link.setAttribute('data-gallery', 'user-gallery');

            const img = document.createElement('img');
            img.src = photo.url;
            img.classList.add('image-preview');

            link.appendChild(img);
            card.appendChild(link);

            const info = document.createElement('div');
            info.classList.add('image-info');
            const date = new Date(photo.uploadDate);
            info.textContent = `Uploaded on ${date.toLocaleDateString()}`;

            card.appendChild(info);
            gallery.appendChild(card);
        });

        GLightbox({ selector: '.glightbox' });

    } catch (error) {
        console.error('Failed to load photos:', error);
        noPhotosMsg.style.display = 'block';
        noPhotosMsg.querySelector('h4').textContent = 'Error loading photos';
        noPhotosMsg.querySelector('p').textContent = 'Please try again later.';
    }
});
