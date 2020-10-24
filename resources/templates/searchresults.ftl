<#import "template.ftl" as layout />
<@layout.mainLayout>
    <div class="container-fluid">
        <ul class="list-group">
        <#list results as result>
            <a href="${result.url()}">
                <li class="list-group-item">${result.formattedName}</li>
            </a>
        </#list>
        </ul>
    </div>
</@layout.mainLayout>