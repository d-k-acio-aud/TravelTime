document.getElementById('registerForm').addEventListener('submit', async function(e) {
    e.preventDefault();

    // Проверка совпадения паролей
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;

    if (password !== confirmPassword) {
        showAlert('Пароли не совпадают!', 'danger');
        return;
    }

    try {
        // Отправка данных
        const response = await fetch('/api/auth/register', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({
                username: document.getElementById('username').value,
                email: document.getElementById('email').value,
                password: password
            }),
            credentials: 'include'
        });

        if (response.ok) {
            // Редирект с сообщением об успехе
            window.location.href = '/login?success=Регистрация прошла успешно! Теперь вы можете войти.';
        } else {
            const errorData = await response.json();
            showAlert(errorData.message || 'Ошибка регистрации', 'danger');
        }
    } catch (error) {
        console.error('Ошибка:', error);
        showAlert('Сетевая ошибка', 'danger');
    }
});

// Функция для показа уведомлений
function showAlert(message, type) {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show position-fixed top-0 start-50 translate-middle-x mt-3`;
    alertDiv.style.zIndex = '1000';
    alertDiv.role = 'alert';
    alertDiv.innerHTML = `
        <strong>${type === 'danger' ? 'Ошибка!' : 'Успех!'}</strong> ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;

    document.body.appendChild(alertDiv);

    setTimeout(() => {
        const alert = bootstrap.Alert.getOrCreateInstance(alertDiv);
        alert.close();
    }, 5000);
}