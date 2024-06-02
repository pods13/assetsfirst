--liquibase formatted sql
--changeset ASSETS.1.00.00.007-001 failOnError:true splitStatements:true context:update runOnChange: false
INSERT INTO tag_category (name, code, color)
values ('Сектор Экономики', 'sector', '#DA627D');

INSERT INTO tag (name, category_id)
SELECT 'Энергетика', id FROM tag_category where code = 'sector';

INSERT INTO tag (name, category_id)
SELECT 'Сырье', id FROM tag_category where code = 'sector';

INSERT INTO tag (name, category_id)
SELECT 'Промышленность', id FROM tag_category where code = 'sector';

INSERT INTO tag (name, category_id)
SELECT 'Потребительские товары повседневного спроса', id FROM tag_category where code = 'sector';

INSERT INTO tag (name, category_id)
SELECT 'Потребительские товары выборочного спроса', id FROM tag_category where code = 'sector';

INSERT INTO tag (name, category_id)
SELECT 'Здравоохранение', id FROM tag_category where code = 'sector';

INSERT INTO tag (name, category_id)
SELECT 'Финансы', id FROM tag_category where code = 'sector';

INSERT INTO tag (name, category_id)
SELECT 'Технологии', id FROM tag_category where code = 'sector';

INSERT INTO tag (name, category_id)
SELECT 'Коммунальные услуги', id FROM tag_category where code = 'sector';

INSERT INTO tag (name, category_id)
SELECT 'Недвижимость', id FROM tag_category where code = 'sector';

--liquibase formatted sql
--changeset ASSETS.1.00.00.007-002 failOnError:true splitStatements:true context:update runOnChange: false
INSERT INTO tag_category (name, code, color)
values ('Отрасль экономики', 'industry', '#D3B855');

INSERT INTO tag (name, category_id)
select ind.*, tc.id
from (SELECT 'Автомобили и запчасти к ним'
      UNION
      SELECT 'Аэрокосмическая и оборонная промышленность'
      UNION
      SELECT 'Банковские услуги'
      UNION
      SELECT 'Биотехнологии и медицинские исследования'
      UNION
      SELECT 'Бумага и изделия из древесины'
      UNION
      SELECT 'Водоснабжающие и коммунальные компании'
      UNION
      SELECT 'Возобновляемая энергия'
      UNION
      SELECT 'Газ и нефть'
      UNION
      SELECT 'Газоснабжение'
      UNION
      SELECT 'Гостиничные и развлекательные услуги'
      UNION
      SELECT 'Деятельность правительства'
      UNION
      SELECT 'Диверсифицированные промышленные оптовые товары'
      UNION
      SELECT 'Диверсифицированные розничные товары'
      UNION
      SELECT 'Еда и табачные изделия'
      UNION
      SELECT 'Жилищное строительство и строительные материалы'
      UNION
      SELECT 'Инвестиционно-банковские и инвестиционные услуги'
      UNION
      SELECT 'Инвестиционные холдинги'
      UNION
      SELECT 'Институты, ассоциации и организации'
      UNION
      SELECT 'Интегрированное аппаратное и программное обеспечение'
      UNION
      SELECT 'Коллективные инвестиции'
      UNION
      SELECT 'Компьютеры, телефоны и бытовая электроника'
      UNION
      SELECT 'Конгломераты — производители потребительских товаров'
      UNION
      SELECT 'Контейнерные перевозки и упаковка'
      UNION
      SELECT 'Металлургия и горнодобывающая промышленность'
      UNION
      SELECT 'Многопрофильные коммунальные компании'
      UNION
      SELECT 'Напитки'
      UNION
      SELECT 'Неклассифицированные поставщики образовательных услуг'
      UNION
      SELECT 'Оборудование и др. поставки для системы здравоохранения'
      UNION
      SELECT 'Оборудование и услуги, связанные с нефтью и газом'
      UNION
      SELECT 'Оборудование, инструменты, автомобили большой грузоподъемности, железнодорожный и водный транспорт'
      UNION
      SELECT 'Операции с недвижимостью'
      UNION
      SELECT 'Офисное оборудование'
      UNION
      SELECT 'ПИФы в сфере жилищной и коммерческой недвижимости'
      UNION
      SELECT 'Полупроводники и полупроводниковое оборудование'
      UNION
      SELECT 'Программное обеспечение и ИТ-услуги'
      UNION
      SELECT 'Профессиональное и бизнес-образование'
      UNION
      SELECT 'Профессиональные и коммерческие услуги'
      UNION
      SELECT 'Розничная торговля едой и лекарствами'
      UNION
      SELECT 'СМИ и издательское дело'
      UNION
      SELECT 'Связь и сети'
      UNION
      SELECT 'Специализированные розничные торговцы'
      UNION
      SELECT 'Страхование'
      UNION
      SELECT 'Строительные материалы'
      UNION
      SELECT 'Строительство и проектирование'
      UNION
      SELECT 'Текстиль и одежда'
      UNION
      SELECT 'Телекоммуникационные услуги'
      UNION
      SELECT 'Товары для досуга'
      UNION
      SELECT 'Товары и услуги личного пользования и хозяйственного назначения'
      UNION
      SELECT 'Транспортная инфраструктура'
      UNION
      SELECT 'Транспортные и логистические услуги'
      UNION
      SELECT 'Угледобывающая промышленность'
      UNION
      SELECT 'Уран'
      UNION
      SELECT 'Услуги пассажирских перевозок'
      UNION
      SELECT 'Учреждения и услуги здравоохранения'
      UNION
      SELECT 'Фармацевтические компании'
      UNION
      SELECT 'Финансовые технологии (финтех) и инфраструктура'
      UNION
      SELECT 'Химическая промышленность'
      UNION
      SELECT 'Хозяйственные товары'
      UNION
      SELECT 'Школы, колледжи и университеты'
      UNION
      SELECT 'Электронное оборудование и запчасти'
      UNION
      SELECT 'Энергетические компании и независимые производители электроэнергии'
     ) ind
         join tag_category tc
where code = 'industry';

--liquibase formatted sql
--changeset ASSETS.1.00.00.007-003 failOnError:true splitStatements:true context:update runOnChange: false

ALTER TABLE instrument
DROP FOREIGN KEY fk__instrument__company_id__company;
ALTER TABLE instrument DROP COLUMN company_id;

--liquibase formatted sql
--changeset ASSETS.1.00.00.007-004 failOnError:true splitStatements:true context:update runOnChange: false

ALTER TABLE company
DROP FOREIGN KEY fk__company__industry_id__industry;

ALTER TABLE industry
DROP FOREIGN KEY fk__industry__sector_id__sector;
DROP TABLE sector;
DROP TABLE industry;
DROP TABLE company;
