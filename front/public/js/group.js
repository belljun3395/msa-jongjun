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

    let allGroups = await fetch('http://localhost:8765/group/groups', {
        method: 'GET',
        cache: 'no-cache',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'Authorization': accessTokenValue,
        },
        credentials: 'include',
    }).then(res => {
        if (!res.ok) {
            alert("error");
        }
        return res;
    });

    let allGroupsInfo = await allGroups.json();

    for (let i = 0; i < allGroupsInfo.data.length; i++) {
        let groupElement = document.createElement("a");
        groupElement.innerHTML =
            `<h3 className="mb-0" id = "group">`
            + allGroupsInfo.data[i].groupName + `
                    <span className="text-primary">`
            + await getName(allGroupsInfo.data[i].ownerId) + `
                    </span>`
            + `<input type="hidden" id="groupIdV" value="${allGroupsInfo.data[i].groupId}"/>`
            + `<input type="button" id="groupId" value="in"> </h3>`;
        document.querySelector(".resume-section-content")
            .append(groupElement);
    }

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
    memberId = member.data.memberId;

    let nameElement = document.createElement("a");
    nameElement.innerHTML = `<a class="nav-link js-scroll-trigger" id="name" href="/user">` + name + `</a>`;

    document.querySelector(".navbar-nav")
        .prepend(nameElement);


     let groups = document.querySelectorAll("#groupId");
    for (let i = 0; i < groups.length; i++) {
        groups[i].onclick = () => {
            groupJoin(i);
        };
    }

    async function groupJoin(i) {
        let elementNodeListOf = document.querySelectorAll("#groupIdV");
        let groupId = elementNodeListOf[i].value;
        await fetch('http://localhost:8765/group/groups/members' + memberId, {
            method: 'POST',
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


async function getName() {
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

const makeGroup = document.getElementById("makeGroup");

makeGroup.addEventListener("click", () => {
    location.href = "/group/info";
});


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