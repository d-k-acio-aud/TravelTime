function setupLoginForm() {
    document.getElementById('loginForm').addEventListener('submit', async function(e) {
        e.preventDefault();


        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({
                email: document.getElementById('email').value,
                password: document.getElementById('password').value
            }),
            credentials: 'include'
        });


        if (response.ok) {
            window.location.href = '/home';
        } else {
            alert('Ошибка входа!');
        }
    });


    // Показываем сообщение об успешной регистрации, если оно есть в URL
    const urlParams = new URLSearchParams(window.location.search);
    const successMessage = urlParams.get('success');


    if (successMessage) {
        showSuccessAlert(successMessage);
    }
}


function showSuccessAlert(message) {
    const alertDiv = document.createElement('div');
    alertDiv.className = 'alert alert-success alert-dismissible fade show position-fixed top-0 start-50 translate-middle-x mt-3';
    alertDiv.style.zIndex = '1000';
    alertDiv.role = 'alert';
    alertDiv.innerHTML = `
       <strong>Успех!</strong> ${message}
       <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
   `;


    document.body.appendChild(alertDiv);


    // Автоматически скрываем через 5 секунд
    setTimeout(() => {
        const alert = bootstrap.Alert.getOrCreateInstance(alertDiv);
        alert.close();
    }, 5000);
}


document.addEventListener('DOMContentLoaded', setupLoginForm);

