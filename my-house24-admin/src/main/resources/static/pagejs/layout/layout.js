const currentUrl = window.location.href;
const myArray = currentUrl.split("/");
var root = myArray[3];
$(document).ready(function () {
    $("#roleName").text(getRoleName(roles[0].authority));
    const currHref = window.location.pathname;
    $('.menu-item a').each(function (i, item) {
        if (currHref.includes($(item).attr('href'))) {
            $(item).parent().addClass('active')
            if (currHref.includes("site-management") || currHref.includes("system-settings")) {
                $(this).parent().parent().parent().addClass("active open");
            }
        }
    });

    $.ajax({
        type: "GET",
        url: "/" + root + "/admin/getPermissions",
        data: {
            role: roles[0].authority
        },
        success: function (response) {
            showMenuItems(response);
        },
        error: function () {
            toastr.error(errorMessage);
        }
    });
});

function getRoleName(role) {
    switch (role) {
        case 'ROLE_DIRECTOR':
            return directorRole;
        case 'ROLE_MANAGER':
            return managerRole;
        case 'ROLE_ACCOUNTANT':
            return accountantRole;
        case 'ROLE_ELECTRICIAN':
            return electricianRole;
        case 'ROLE_PLUMBER':
            return plumberRole;
    }
}

function showMenuItems(permissions) {

    let i = 0;
    let count = 0;
    $(".menu-item a").each(function () {
        let aHref = $(this).attr("href");
        if (permissions[i].allowed === false) {
            $(this).parent().hide();
            if (aHref.includes("site-management")) {
                $(this).parent().parent().parent().hide();
            }
        }

        if (!aHref.includes("site-management") && aHref.includes("/")) {
            i++;
        } else if (aHref.includes("site-management") && count === 0) {
            i++;
            count++;
        }
    })
}

$("#logoutLink").on("click", function (e) {
    e.preventDefault();
    if ($('#logoutModal').length === 0) {
        $("div.card").append(
            `<div class="modal fade" id="logoutModal" tabindex="-1" aria-labelledby="exampleModalLabel"
             aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="btn-close" data-bs-dismiss="modal"
                                aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <h4>${logoutModalMessage}</h4>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-danger" onclick="logout()" id="logoutButton"">
                            ${logoutModalYes}
                        </button>
                    </div>
                </div>
            </div>
        </div>`
        )
    }
    $('#logoutModal').modal('show');
});

function logout() {
    window.location = $('#logoutLink').attr('href');
}

$(window).on('load, ajaxStop', function () {
    $('img').on('error', function () {
        $(this).attr('src', defaultPlaceholderImage);
    })
})
