<#import "template.ftl" as layout />
<@layout.mainLayout>



    <table class="table">
  <thead>
    <tr>
      <th scope="col">ID</th>
      <th scope="col">Title</th>
      <th scope="col">First Name</th>
      <th scope="col">Last Name</th>
      <th scope="col">Nick Name</th>
      <th scope="col">Photos</th>
      <th scope="col">Save</th>
    </tr>
  </thead>
  <tbody>
      <#list cops as cop >
          <tr>
              <form action="/manage/edit" method="post" autocomplete="off" ></form>
              <th scope="row"><input type="">${cop.id}</th>
              <td><input type="text"></td>
              <td>Otto</td>
              <td>@mdo</td>
          </tr>
      </#list>


    <tr>
      <th scope="row">2</th>
      <td>Jacob</td>
      <td>Thornton</td>
      <td>@fat</td>
    </tr>
    <tr>
      <th scope="row">3</th>
      <td>Larry</td>
      <td>the Bird</td>
      <td>@twitter</td>
    </tr>
  </tbody>
</table>

</@layout.mainLayout>