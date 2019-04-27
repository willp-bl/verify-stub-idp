<div class="main">
    <h1>Welcome to ${name}</h1>
    <#if userLoggedIn>
        <p>Welcome ${userFullName}</a></p>
        <p><a href="${logoutResource}">Logout</a></p>
    <#else>
        <p><a href="${preRegisterResource}">Create ${anOrA} ${name} identity account</a></p>
        <p><a href="${loginResource}">Log In</a></p>
    </#if>
    <p><a href="${startPromptResource}">List of available single-idp services</a></p>
    <p><a href="#">Do other stuff with this IDP</a> (which will just leave you on this page because it's a stub-idp)</p>
</div>
