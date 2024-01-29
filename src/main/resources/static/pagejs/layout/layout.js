$(document).ready(function () {
    $.ajax({
        type: "GET",
        url: "/admin/getPermissions",
        data: {
            role: roles[0].authority
        },
        success: function (response) {
            showMenuItems(response);
        },
        error: function (){
            toastr.error(errorMessage);
        }
    });
});

function showMenuItems(permissions){
    var pathname = window.location.pathname;
    let i = 0;
    let count = 0;
    $(".menu-link").each(function () {
        let aHref = $(this).attr("href");
        if(permissions[i].allowed === false && aHref.includes("/")){
            $(this).parent().hide();
            if(aHref.includes("site-management")){
                $(this).parent().parent().parent().hide();
            }
        } else {
            if (aHref === pathname.slice(0, aHref.length)) {
                if ($(this).parent().parent().attr("class") === "menu-sub") {
                    $(this).parent().parent().parent().addClass("active open");
                }
                $(this).parent().addClass("active");
            }
        }
        if(!aHref.includes("site-management") && aHref.includes("/")) {
            i++;
        } else if(aHref.includes("site-management") && count === 0 ){
            i++;
            count++;
        }
    })
}

$("#logoutLink").on("click", function (e) {
    e.preventDefault();
    $("div.card").append(
        '<div class="modal fade" id="logoutModal" tabindex="-1" aria-labelledby="exampleModalLabel"\n' +
        '             aria-hidden="true">\n' +
        '            <div class="modal-dialog modal-dialog-centered">\n' +
        '                <div class="modal-content">\n' +
        '                    <div class="modal-header">\n' +
        '                        <button type="button" class="btn-close" data-bs-dismiss="modal"\n' +
        '                                aria-label="Close"></button>\n' +
        '                    </div>\n' +
        '                    <div class="modal-body">\n' +
        '                        <h4>Ви впевнені що хочете вийти?</h4>\n' +
        '                    </div>\n' +
        '                    <div class="modal-footer">\n' +
        '                        <button type="button" class="btn btn-danger" onclick="logout()" id="logoutButton"">\n' +
        '                            Так\n' +
        '                        </button>\n' +
        '                    </div>\n' +
        '                </div>\n' +
        '            </div>\n' +
        '        </div>'
    )
    $('#logoutModal').modal('show');
});
function logout () {
    window.location = $('#logoutLink').attr('href');
}
