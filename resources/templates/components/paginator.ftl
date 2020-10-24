<nav aria-label="Page navigation example">
    <ul class="pagination justify-content-center">
        <li class="page-item <#if page == 0>disabled</#if>">
            <a class="page-link" href="/cops/${page - 1}" tabindex="-1" <#if page == 0>aria-disabled="true"</#if> >Previous</a>
        </li>
        <#list pages as p>
            <li class="page-item <#if page == p>active</#if>"<#if page == p>aria-current="page" </#if>>
                <a class="page-link" href="/cops/${p}">${p}</a>
            </li>

        </#list>
        <li class="page-item
<#if page == numPages>
disabled
</#if>
">
            <a class="page-link"
                    <#if page == numPages>
                    aria-disabled="true"
                    </#if>
             href="/cops/${page + 1}">Next</a>
        </li>
    </ul>

</nav>