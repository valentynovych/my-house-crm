$(document).on('ajaxStart', function () {
    $('.card-body').block({
        message: `<div class="col d-flex justify-content-center ">
                 <!-- Wave -->
                 <div class="sk-wave sk-primary">
                   <div class="sk-wave-rect"></div>
                   <div class="sk-wave-rect"></div>
                   <div class="sk-wave-rect"></div>
                   <div class="sk-wave-rect"></div>
                   <div class="sk-wave-rect"></div>
                 </div>
               </div>`,
        css: {
            backgroundColor: "transparent",
            border: "0",
            position: "center"
        },
        overlayCSS: {
            backgroundColor: "#fff",
            opacity: 0.8
        }
    })
});

$(document).on('ajaxStop', function () {
    $('.card-body').unblock();
})