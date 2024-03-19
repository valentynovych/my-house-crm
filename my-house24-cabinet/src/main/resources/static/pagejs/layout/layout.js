$(document).ready(function () {
    const currHref = window.location.pathname;
    $('.menu-item a').each(function (i, item) {
        if (currHref.includes($(item).attr('href'))) {
            $(item).parent().addClass('active')
            $(this).parents('[data-parrent]').addClass("active open");
        }
    });
});

$("#logoutLink").on("click", function (e) {
    e.preventDefault();
    if ($('#logoutModal').length === 0) {
        $("div.card").append(`<div class="modal fade" id="logoutModal" tabindex="-1" aria-labelledby="exampleModalLabel"
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
        </div>`)
    }
    $('#logoutModal').modal('show');
});

function logout() {
    window.location = $('#logoutLink').attr('href');
}