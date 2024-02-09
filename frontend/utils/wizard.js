$(document).ready(function () {
    //Initialize tooltips
    $('.nav-tabs > li a[title]').tooltip();
    
    //Wizard
    $('.wizard a[data-toggle="tab"]').on('show.bs.tab', function (e) {
        //if disabled, do not show the tab content
        var $target = $(e.target);    
        if ($target.parent().hasClass('disabled')) {
            return false;
        }
        //if not disable, show/hide the buttons depending on the position of the tab
        if ($target.parent().prev().length == 0){
            $(".prev-step").hide();
        }else{
            $(".prev-step").show();
        }
        if ($target.parent().next().length == 0){
            $(".next-step").hide();
            $(".final-step").show();
        }else{
            $(".next-step").show();
            $(".final-step").hide();
        }
    });

    //default functionality to the next/prev buttons
    /*
     $(".next-step").click(function (e) {
        var $active = $('.wizard .nav-tabs li.active');
        $active.next().removeClass('disabled');
        nextTab($active);

    });
    $(".prev-step").click(function (e) {
        var $active = $('.wizard .nav-tabs li.active');
        prevTab($active);

    });*/
});

function nextTab(elem) {
    $(elem).next().find('a[data-toggle="tab"]').click();
}
function prevTab(elem) {
    $(elem).prev().find('a[data-toggle="tab"]').click();
}

