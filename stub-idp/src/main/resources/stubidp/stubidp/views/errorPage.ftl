<#-- @ftlvariable name="" type="stubidp.stubidp.views.ErrorPageView" -->
<html>
<head>
    <title>Sorry, something went wrong</title>
    <meta charset="utf-8"/>
    <meta content="width=device-width,initial-scale=1.0" name="viewport"/>
</head>
<style>
    .main {
        color: rgb(51, 51, 51);
        font-family: "Helvetica Neue", Arial, Helvetica, sans-serif;
        text-align: center;
        margin-top: 20px;
        margin-bottom: 20px;
    }
    .logo {
        /* maximum that displays in Chrome/macOS */
        font-size: 7em;
        margin-top: 20px;
        margin-bottom: 20px;
    }
    .title {
        font-weight: 700;
        font-size: 3em;
        margin-top: 20px;
        margin-bottom: 20px;
    }
    .issues {
        font-weight: 300;
        margin-top: 20px;
        margin-bottom: 20px;
    }
    .help {
        margin-top: 20px;
        margin-bottom: 20px;
    }
</style>
<body class="main">
<div class="logo">${reaction}</div>
<div class="title">Sorry, something went wrong</div>
<div class="issues">
Possible issues:<br>
Your session is unknown (it might have expired); or<br>
There is a problem with your cookies; or<br>
Another technical problem has arisen.<br>
</div>
<div class="help">Go back to a service and try again.</div>
</div>
</div>
<hr>
<footer>
    <div class="container" id="links">
        <a href="https://github.com/alphagov/verify-stub-idp">Source Code</a> - Built by the Government Digital Service
    </div>
</footer>
</body>
</html>
