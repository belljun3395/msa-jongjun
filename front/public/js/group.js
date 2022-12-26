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
            alert("다시 로그인 해주세요");
            location.href = "/login";
        }
    });

    let accessTokenInfo = await accessToken.json();
    let memberId = accessTokenInfo.memberId;
    accessTokenValue = accessTokenInfo.accessTokenValue;

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


     let groups = document.querySelectorAll("#groupId");
    for (let i = 0; i < groups.length; i++) {
        groups[i].onclick = () => {
            groupJoin(i);
        };
    }

    async function groupJoin(i) {
        let elementNodeListOf = document.querySelectorAll("#groupIdV");
        let groupId = elementNodeListOf[i].value;
        await fetch('http://localhost:8765/group/groups/members', {
            method: 'POST',
            cache: 'no-cache',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
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