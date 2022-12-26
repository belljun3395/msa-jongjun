const loginForm = document.querySelector("#login-form");

loginForm.addEventListener("submit", getLogin);

async function getLogin(event) {
    event.preventDefault();
    const emailV = document.getElementById('email').value;
    const passwordV = document.getElementById('password').value;


    const loginInfo = {
        email: emailV,
        password: passwordV,
        clientType: "mobile",
        location: "korea"
    }


    await fetch('http://localhost:8765/auth/members/login', {
        method: 'POST',
        cache: 'no-cache',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        credentials: 'include',
        body: new URLSearchParams(loginInfo)
    }).then((res) => {
        if (!res.ok) {
            alert(res.status);
            location.href = '/login';
        } else {
            location.href = "/user";
        }
    });

}