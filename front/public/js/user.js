let accessTokenValue;
window.onload = async function () {

    let accessToken = await fetch('http://localhost:8765/auth/members/token/renewal', {
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

    accessTokenValue = await accessToken.text();

    let memberInfo = await fetch('http://localhost:8765/auth/members/', {
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
    let memberId = member.data.memberId;

    let nameElement = document.createElement("a");
    nameElement.innerHTML = `<a class="nav-link js-scroll-trigger" id="name" href="/user">` + name + `</a>`;

    document.querySelector(".nav-item")
        .prepend(nameElement);

    let groupsInfo = await fetch('http://localhost:8765/group/groups/members/' + memberId, {
        method: 'GET',
        cache: 'no-cache',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'Authorization': accessTokenValue,
        },
        credentials: 'include',
    });
    let groups = await groupsInfo.json();

    for (let i = 0; i < groups.data.length; i++) {
        let groupElement = document.createElement("a");
        groupElement.innerHTML =
            `<h3 className="mb-0" id = "group">`
            + groups.data[i].groupName + `
                    <span className="text-primary">`
            + await getName(groups.data[i].ownerId) + `
                    </span>`
            + `<input type="hidden" id="groupIdV" value="${groups.data[i].groupId}"/>`
            + `<input type="button" id="groupId" value="out"> </h3>`;
        document.querySelector(".resume-section-content")
            .append(groupElement);
    }

    let groupIdsElement = document.querySelectorAll("#groupId");
    for (let i = 0; i < groupIdsElement.length; i++) {
        groupIdsElement[i].onclick = () => {
            groupOut(i);
        };
    }

    async function groupOut(i) {
        let groupIdsElement = document.querySelectorAll("#groupIdV");
        let groupId = groupIdsElement[i].value;
        await fetch('http://localhost:8765/group/groups/members', {
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
    let memberInfo = await fetch('http://localhost:8765/auth/members/', {
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