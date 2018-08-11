
psql -U folio_admin -h localhost okapi_modules

select * from diku_olf_erm.title_instance where ti_title like 'cancer%';


select ti_title from diku_olf_erm.title_instance where ti_title like 'Clini%';


select 'Dump all package, title....'

select pkg_name, ti_title
from diku_olf_erm.package_content_item,
     diku_olf_erm.package,
     diku_olf_erm.platform_title_instance,
     diku_olf_erm.title_instance
where pci_pkg_fk = pkg_id
  and pci_pti_fk = pti_id
  and pti_ti_fk = ti_id;


select 'Dump all package, title, platform....'

select pkg_name, ti_title, pt_name
from diku_olf_erm.package_content_item,
     diku_olf_erm.package,
     diku_olf_erm.platform_title_instance,
     diku_olf_erm.title_instance,
     diku_olf_erm.platform
where pci_pkg_fk = pkg_id
  and pci_pti_fk = pti_id
  and pti_ti_fk = ti_id
  and pti_pt_fk = pt_id;


select 'List all remote KB sources'

select * from diku_olf_erm.remotekb;
