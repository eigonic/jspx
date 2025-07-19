var $jspx = jQuery.noConflict();
$jspx(document).ready(function() {
	// $jspx = jQuery.noConflict();
	if ($jspx.browser.msie)
		window.attachEvent("onclick", hideActivePanel);
	else
		window.addEventListener("click", hideActivePanel, false);
	if (jQuery.livequery)
		jQuery.livequery.registerPlugin("raty");
	//initiateButtons();
	$jspx('.jspx_long_text_trimed').popover({
		placement : 'bottom',
		container: 'body'
	});
});

function loadJQueryIfNot(url) {
	// Only do anything if jQuery isn't defined
	if (typeof jQuery == 'undefined') {
		var script = document.createElement('script');
		script.src = url;

		var head = document.getElementsByTagName('head')[0], done = false;

		// Attach handlers for all browsers
		script.onload = script.onreadystatechange = function() {
			if (!done && (!this.readyState || this.readyState == 'loaded' || this.readyState == 'complete')) {
				done = true;

				script.onload = script.onreadystatechange = null;
				head.removeChild(script);
			}
		};
		head.appendChild(script);
	}
}

function selectAll(elementPrefix, element) {
	var isCheck = element.checked == "checked" || element.checked == true;
	$jspx('input[type=checkbox][id*=' + elementPrefix + '][id!=' + element.id + ']').each(function(index) {
		var checkElement = $jspx(this).get(0);
		if (isCheck)
			checkElement.disabled = "true";
		else
			checkElement.removeAttribute("disabled");
	});
}

function checkAll(elementPrefix, element) {
	var isCheck = element.checked == "checked" || element.checked == true;
	$jspx('input[type=checkbox][id*=' + elementPrefix + '][id!=' + element.id + ']').each(function(index) {
		var checkElement = $jspx(this).get(0);
		if (isCheck)
			checkElement.checked = "checked";
		else
			checkElement.removeAttribute("checked");
	});
}

function hideActivePanel(eventArg) {
	var targetId;
	if ($jspx.browser.msie)
		targetId = window.event.srcElement.id;
	else
		targetId = eventArg.target.id;

	if (targetId != null && targetId == activeLinkImageId) {
		if (!$jspx.browser.msie)
			routeEvent(eventArg);
		return true;
	}
	setActivePanel(null, null);
}
function setActivePanel(activeP, activeLink) {
	try {
		$jspx('#' + activePanel).hide();
		$jspx('#' + activeLinkImageId).removeClass('jspx_ms').addClass('jspx_ps');
	} catch (e) {
	}
	if (activeP != activePanel) {

		activePanel = activeP;
		activeLinkImageId = activeLink;
	} else {
		activePanel = null;
		activeLinkImageId = null;

	}
}
var activePanel;
var activeLinkImageId;

// this function create an Array that contains the JS code of every <script> tag
// in parameter
// then apply the eval() to execute the code in every script collected
function parseScript(strcode) {
	var scripts = new Array(); // Array which will store the script's code

	// Strip out tags
	while (strcode.indexOf("<script") > -1 || strcode.indexOf("</script") > -1) {
		var s = strcode.indexOf("<script");
		var s_e = strcode.indexOf(">", s);
		var e = strcode.indexOf("</script", s);
		var e_e = strcode.indexOf(">", e);

		// Add to scripts array
		scripts.push(strcode.substring(s_e + 1, e));
		// Strip from strcode
		strcode = strcode.substring(0, s) + strcode.substring(e_e + 1);
	}

	// Loop through every script collected and eval it
	for (var i = 0; i < scripts.length; i++) {
		try {
			jQuery.globalEval(scripts[i]);
		} catch (ex) {
			// do what you want here when a script fails
			alert(ex);
		}
	}
}

/**
 * validates required fields to be used in the DATA table detailed panel.
 * 
 * @param ids
 * @returns
 */
function validInputData(ids) {
	if (!ids)
		return true;
	var isValid = true;
	var isFocused = false;
	for (var i = 0; i < ids.length; i++) {
		var id = '#' + ids[i];
		if ($app(id).val() == "") {
			isValid = false;
			$app(id).css("backgroundColor", "#FFBABA");
			$app(id).css("borderColor", "#D8000C");
			if (!isFocused) {
				$app(id).focus();
				isFocused = true;
			}
		}
	}
	return isValid;
}

function initiateButtons() {
	$jspx('[data-loading-text]').each(function() {
		$jspx(this).click(function() {
			$jspx(this).button('loading');
		});
	});
}

function jspxAutoComplete(panelId, controlId, textVal, onchangeEvent) {
	//remove the onchange first
	//var ev = $jspx('#' + controlId).attr('onchange');
	$jspx('#' + controlId).attr('onchange', '');
	$jspx('#' + controlId).val(textVal);

	//$jspx('#' + controlId).attr('onchange', ev);
	//$jspx('#' + controlId).change();
	$jspx('#' + panelId).hide();
	try {
		eval(onchangeEvent);
	} catch (e) {
	}
}