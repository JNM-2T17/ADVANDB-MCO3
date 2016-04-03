ALTER TABLE hpq_hh
	ADD INDEX hhidx_2(id),
    ADD INDEX hhidx_3(tenur);
ALTER TABLE hpq_alp
	ADD INDEX alpidx_1(hpq_hh_id);
DELETE FROM db_hpq_marinduque.hpq_hh WHERE id = 407001;
DELETE FROM db_hpq.hpq_hh WHERE id = 407001 aND wall = 4 and roof = 1;
DELETE FROM db_hpq.hpq_hh WHERE id = 167551 AND wall = 2 AND roof = 1;
DELETE FROM db_hpq_palawan.hpq_hh WHERE id = 167551 AND wall = 2 AND roof = 1;
