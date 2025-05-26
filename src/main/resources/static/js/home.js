// В обработчике отправки формы измените сбор данных:
const tripData = {
    name: document.getElementById('tripName').value,
    destination: document.getElementById('destination').value,
    startDate: document.getElementById('startDate').value,
    endDate: document.getElementById('endDate').value,
    description: document.getElementById('description').value // Добавлено
};

document.addEventListener('DOMContentLoaded', function() {
    // Обработка кнопки добавления трипа
    document.getElementById('addFirstTrip')?.addEventListener('click', function(e) {
        e.preventDefault();
        // Здесь можно открыть модалку или перейти на страницу создания
        window.location.href = '/trips/new';
    });

    // Логика для логаута (если нужно что-то дополнительное)
    // Обработка логаута с подтверждением
    document.querySelector('form[action*="logout"]')?.addEventListener('submit', function(e) {
        e.preventDefault();

        if (confirm('Are you sure you want to log out?')) {
            // Отправляем форму
            this.submit();

            // Дополнительные действия после логаута
            setTimeout(() => {
                window.location.href = '/login?logout=true';
            }, 500);
        }
    });

});