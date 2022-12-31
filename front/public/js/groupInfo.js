let memberId;
let accessTokenValue;

window.onload = async function () {

    let accessToken = await fetch('http://localhost:8765/auth/tokens/access', {
        method: 'GET',
        cache: 'no-cache',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        credentials: 'include',
    }).then(res => {
        if (!res.ok) {
            alert("error");
        }
        return res;
    });


    let accessTokenInfo = await accessToken.json();
    memberId = accessTokenInfo.memberId;
    accessTokenValue = accessTokenInfo.accessTokenValue;

    let memberInfo = await fetch('http://localhost:8765/auth/members/' + memberId, {
        method: 'GET',
        cache: 'no-cache',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'Authorization': accessTokenValue,
        },
        credentials: 'include',
    });
    member = await memberInfo.json();
    let name = member.data.name;

    let nameElement = document.createElement("a");
    nameElement.innerHTML = `<a class="nav-link js-scroll-trigger" id="name" href="/user">` + name + `</a>`;

    document.querySelector(".navbar-nav")
        .prepend(nameElement);

};


const groupForm = document.querySelector("#group-form");

groupForm.addEventListener("submit", groupMake);

async function groupMake(event) {
    event.preventDefault();
    let groupName = document.getElementById("groupName").value;
    let maxMember = document.getElementById("maxMember").value;
    await fetch('http://localhost:8765/group/groups/admin', {
        method: 'POST',
        cache: 'no-cache',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'Authorization': accessTokenValue,
        },
        credentials: 'include',
        body: new URLSearchParams({groupName: groupName, maxMember: maxMember, ownerId: memberId}),
    }).then(res => {
        if (!res.ok) {
            alert("권한이 없습니다.");
            location.href = "/group"
        }
    }).then(() => {
        location.href = '/group';
    });
}