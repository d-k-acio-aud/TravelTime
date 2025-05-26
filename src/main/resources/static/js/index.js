document.addEventListener('DOMContentLoaded', function() {
    // Анимация при скролле
    const animateOnScroll = () => {
        const featureCards = document.querySelectorAll('.feature-card');

        featureCards.forEach((card, index) => {
            setTimeout(() => {
                card.style.opacity = '1';
                card.style.transform = 'translateY(0)';
            }, 200 * index);
        });
    };

    // Инициализация анимации
    const featureCards = document.querySelectorAll('.feature-card');
    featureCards.forEach(card => {
        card.style.opacity = '0';
        card.style.transform = 'translateY(20px)';
        card.style.transition = 'all 0.5s ease-out';
    });

    // Запускаем анимацию при загрузке
    setTimeout(animateOnScroll, 500);

    // Можно добавить обработчики для других интерактивных элементов
});