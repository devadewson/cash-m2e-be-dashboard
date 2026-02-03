CREATE INDEX idx_dp_account_date ON tbl_portfolio_dp (account_no, dt_created);
CREATE INDEX idx_trd_cif_no ON tbl_portfolio_trd (cif_no);
CREATE INDEX idx_acct_typ_cd ON tbl_mt_acct_typ (cd);
CREATE INDEX idx_acct_prod_typ_cd ON tbl_mt_acct_prod_typ (cd);
