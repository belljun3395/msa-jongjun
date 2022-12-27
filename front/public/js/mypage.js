let accessTokenValue;
window.onload = async function () {

    let accessToken = await fetch('http://localhost:8765/auth/tokens/access', {
        method: 'GET',
        cache: 'no-cache',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        credentials: 'include',
    });


    let accessTokenInfo = await accessToken.json();
    let memberId = accessTokenInfo.memberId;
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
    let email = member.data.email;
    let role = member.data.role;

    let nameElement = document.createElement("a");
    nameElement.innerHTML = `<a class="nav-link js-scroll-trigger" id="name" href="/user">` + name + `</a>`;

    document.querySelector(".nav-item")
        .prepend(nameElement);

    let memberElement = document.createElement("a");
    memberElement.innerHTML =
        `<h3 className="mb-0" id = "group">`
        + name + `
        <span className="text-primary">`
        + email
        + `</span>`
        + `</h3>`
        + `<span className="text-primary">`
        + role
        + `</span>`;
    if (role === "MEMBER") {
        memberElement.innerHTML +=
            `<input type="button" id="adjustRole" value="인증">`
            + `</span>`;
    } else {
        memberElement.innerHTML +=
            `</span>`;
    }
    document.querySelector(".resume-section-content")
        .append(memberElement);


    let adjustRoleElement = document.querySelector("#adjustRole");

    adjustRoleElement.addEventListener("click", makeInputKey, {once: true});

    async function makeInputKey() {
        let sendEmail = await fetch('http://localhost:8765/auth/members/email', {
            method: 'POST',
            cache: 'no-cache',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'Authorization': accessTokenValue,
            },
            credentials: 'include',
            body: new URLSearchParams({email: email, memberId: memberId}),
        });

        let htmlAnchorElement = document.createElement("a");
        htmlAnchorElement.innerHTML = `<input type="text" id="roleKey"> <input type="button" id="submit" value="submit">`;
        document.querySelector(".resume-section-content").append(htmlAnchorElement);

        let uuid = await sendEmail.text();

        let roleKey = document.querySelector("#submit");
        roleKey.addEventListener("click", sendKey, {once: true});

        async function sendKey() {
            let keyValue = document.querySelector("#roleKey").value;
            let sendKey = await fetch('http://localhost:8765/auth/members/email/key', {
                method: 'POST',
                cache: 'no-cache',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'Authorization': accessTokenValue,
                },
                credentials: 'include',
                body: new URLSearchParams({uuid: uuid, authKey: keyValue}),
            });
            if (sendKey) {
                await fetch('http://localhost:8765/auth/members/role', {
                    method: 'POST',
                    cache: 'no-cache',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                        'Authorization': accessTokenValue,
                    },
                    credentials: 'include',
                    body: new URLSearchParams({memberId: memberId, role: "admin"}),
                });
                location.href = '/mypage';
            } else {
                alert("인증실패");
            }
        }
    }


    let groupInfo = await fetch('http://localhost:8765/group/groups/' + memberId, {
        method: 'GET',
        cache: 'no-cache',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'Authorization': accessTokenValue,
        },
        credentials: 'include',
    });
    let group = await groupInfo.json();

    for (let i = 0; i < group.data.length; i++) {
        let groupElement = document.createElement("a");
        groupElement.innerHTML =
            `<h3 className="mb-0" id = "group">`
            + group.data[i].groupName + `
                    <span className="text-primary">`
            + await getName(group.data[i].ownerId) + `
                    </span>`
            + `<input type="hidden" id="groupIdV" value="${group.data[i].groupId}"/>`
            + `<input type="button" id="groupId" value="delete"> </h3>`;
        document.querySelector(".resume-section-content")
            .append(groupElement);
    }


    let elementNodeListOf = document.querySelectorAll("#groupId");
    for (let i = 0; i < elementNodeListOf.length; i++) {
        elementNodeListOf[i].onclick = () => {
            groupDelete(i);
        };
    }
    async function groupDelete(i) {
        let elementNodeListOf = document.querySelectorAll("#groupIdV");
        let groupId = elementNodeListOf[i].value;
        await fetch('http://localhost:8765/group/groups/admin', {
            method: 'DELETE',
            cache: 'no-cache',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'Authorization': accessTokenValue,
            },
            credentials: 'include',
            body: new URLSearchParams({groupId: groupId, memberId: memberId}),
        }).then(() => {
            location.href = "/group";
        });
    }
};

async function getName(memberId) {
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
    return member.data.name;
}


const logoutForm = document.getElementById("logout");

logoutForm.addEventListener("click", logout);

async function logout() {
    await fetch('http://localhost:8765/auth/members/logout', {
        method: 'POST',
        cache: 'no-cache',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'Authorization': accessTokenValue,
        },
        credentials: 'include',
    }).then(() => {
        location.href = '/';
    });
}


