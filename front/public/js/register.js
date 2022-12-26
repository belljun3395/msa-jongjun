const registerForm = document.querySelector("#register-form");

registerForm.addEventListener("submit", getRegister);

async function getRegister(event) {
    event.preventDefault();
    const emailV = document.getElementById('email').value;
    const passwordV = document.getElementById('password').value;
    const nameV = document.getElementById('name').value;
    const roleV = document.getElementById('role').value;


    const registerInfo = {
        email: emailV,
        password: passwordV,
        name: nameV,
        role: roleV,
    }


    let request = await fetch('http://localhost:8765/auth/members/join', {
        method: 'POST',
        cache: 'no-cache',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        credentials: 'include',
        body: new URLSearchParams(registerInfo)
    });

    let response = await request.json();

    if (response.code > 1100) {
        alert(response.message);
        location.href = '/register';
    } else {
        location.href = "/";
    }

}