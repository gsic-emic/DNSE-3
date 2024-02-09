
function setExpandableSections(){
    $("div.information-panel > div.information-panel-title.expandable").each(function(){
        hideSectionContent($(this).parent());
        $(this).click(function(){
            if ($(this).find('.glyphicon-triangle-bottom').length > 0){
                expandSectionContent($(this).parent());
            }else{
                hideSectionContent($(this).parent());
            }
        });
    });
}

function setExpandableSection(divSection){
    hideSectionContent(divSection);
    var section_title = $(divSection).children()[0];
    $(section_title).click(function(){
        if ($(this).find('.glyphicon-triangle-bottom').length > 0){
            expandSectionContent($(this).parent());
        }else{
            hideSectionContent($(this).parent());
        }
    });
}

/**
 * Expand the section's content (its subsections appear contracted)
 * @param divSection the div containing the section to be expanded
 */
function expandSectionContent(divSection){
    var section_title = $(divSection).children()[0];
    var section_content = $(divSection).children()[1];
    //if ($(section_content).is(':visible')==false){
        $(section_content).slideDown("slow");
    //}
    //change the arrow icon for the html heading to the up arrow (hide when clicking)
    if ($(section_title).find('.glyphicon-triangle-bottom').length > 0){
        $(section_title).find('.glyphicon-triangle-bottom').addClass("glyphicon-triangle-top");
        $(section_title).find('.glyphicon-triangle-bottom').removeClass("glyphicon-triangle-bottom");
    }
}

/**
 * Expand the section's content (its subsections appear contracted)
 * @param divSection the div containing the section to be expanded
 */
function hideSectionContent(divSection){
    var section_title = $(divSection).children()[0];
    var section_content = $(divSection).children()[1];
    //if ($(section_content).is(':visible')){
        $(section_content).slideUp("slow");
    //}
    //change the arrow icon for the html heading to the up arrow (hide when clicking)
    if ($(section_title).find('.glyphicon-triangle-top').length > 0){
        $(section_title).find('.glyphicon-triangle-top').addClass("glyphicon-triangle-bottom");
        $(section_title).find('.glyphicon-triangle-top').removeClass("glyphicon-triangle-top");
    }
}
