

document.write("<div><p style='text-align: center; font-size: x-small; color: #aaaaaa;'>&copy; copyright 2011 Tonic Solutions LLC - all rights reserved.</p></div>");
var url = window.location.pathname.replace("/pages/","").replace(".html", "");
location.replace("/?content=" + url);