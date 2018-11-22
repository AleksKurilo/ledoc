alter table if exists main.trades
  add constraint UK_pwhryiheu5xtkgbfm030j2n4p unique (name_en);

alter table main.access_tokens alter column token type varchar(4000);
alter table main.access_tokens alter column new_token type varchar(4000);
alter table main.access_tokens alter column state type varchar(10);

alter table main.employees drop constraint uk_3gqbimdf7fckjbwt1kcud141m;
alter table main.employees add constraint unique_username UNIQUE (username);