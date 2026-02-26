/*
 * © ООО «Наука». Разработчиком и правообладателем настоящего программного обеспечения
 * является общество с ограниченной ответственностью «Наука, технология, информатика, контроль»
 * (сокращенное наименование — ООО «Наука»). Российская Федерация, Санкт-Петербург,
 * ОГРН 1027802483301, ИНН 7804056944. Настоящее программное обеспечение является проприетарным
 * и распространяется исключительно на условиях лицензионного договора с правообладателем.
 *
 * © LLC "NAUKA". The developer and copyright holder of this software is the LLC "NAUKA".
 * Russian Federation, Saint Petersburg, primary registration number 1027802483301,
 * taxpayer ID 7804056944. This software is proprietary and is distributed exclusively
 * under the terms of a license agreement with the copyright holder.
 */
package ru.ntik.demo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import ru.ntik.ancd.dtype.DataType;
import ru.ntik.ancd.dtype.dbms.DvType;
import ru.ntik.ancd.mdata.BusinessObject;
import ru.ntik.ancd.mdata.Column;
import ru.ntik.ancd.mdata.DataSource;
import ru.ntik.ancd.mdata.DeployMode;
import ru.ntik.ancd.mdata.Domain;
import ru.ntik.ancd.mdata.TableEditor;
import ru.ntik.ancd.mdata.dto.DefValueDto;
import ru.ntik.ancd.mdata.migration.MigrationFactory;
import ru.ntik.ancd.mdata.MetaDbBuilder;
import ru.ntik.ancd.mdata.migration.MigrationGenerator;
import ru.ntik.ancd.mdata.migration.operation.MigrationLoader;

/**
 * Демонстрация работы с метаданными через низкоуровневое API
 */
public class DemoMdGen {

    public static void main(String[] args) {
        DemoMdGen runner = new DemoMdGen();
        try {
            runner.run();
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }

    protected String workDir = "./gendata";        // директория куда выгружать результат
    protected MetaDbBuilder builder; // "построитель" БД метаинформации

    protected Domain d_name;   // домейн для наименований бух. объектов

    protected TableEditor accountPlan; // таблица плана счетов
    protected TableEditor currency;    // таблица валют
    protected TableEditor balanceUnit; // Балансовая единица
    protected TableEditor account; // Счета
    protected TableEditor mapAccPlan;   // Мэппинг счета и плана счетов
    protected TableEditor docKind;   // Виды документов
    protected TableEditor mapDkAccount;  // Мэппинг контрагента и БЕ
    protected TableEditor register;  // Регистр
    protected TableEditor registerGroup;  // Группа регистров
    protected TableEditor mapRegGr; // Мэппинг ГР и РЕГ
    protected TableEditor docum; // Документ
    protected TableEditor documPosAbstract; // Позиции документа абстрактный
    protected TableEditor fiDocum; // Финансовый документ
    protected TableEditor entryKind; // Дебет / Кредит
    protected TableEditor entryAbstract; // Проводки абстрактный
    protected TableEditor outcomeItem; // Позиции документа расхода
    protected TableEditor entry62v1; // Проводки 62 счет
    protected TableEditor entry90v1; // Проводки 90 счет

    protected TableEditor partner;   // контрагент
    protected TableEditor contract;  // контракт
    protected TableEditor zvat;      // Ставка НДС
    protected TableEditor mwskz;     // Коды налогов
    protected TableEditor pfm;       // Службы/ПФМ
    protected TableEditor fipos;        // Финансовые позиции
    protected TableEditor costObject;  // Объект учета затрат
    protected TableEditor zfiActivity;  // Вид деятельности

    protected DataSource dataSource; // источник данных (БД, схема БД)

    protected Domain d_pacc;     // домейн (тип данных) кода плана счетов
    protected Domain d_cur;     // домейн (тип данных) валюта
    protected Domain d_bukrs; //домейн код балансовой единицы
//    protected Domain d_ktoks; //домейн тип счета
    protected Domain d_racct; //домейн счет
    protected Domain d_blart; //домейн Виды документов
    protected Domain d_vptnr; //домейн контрагент
    protected Domain d_kostl;  // домейн Объект учета затрат
    protected Domain d_rldnr;  // Регистр
    protected Domain d_ldgrp;  // Группа регистров
    protected Domain d_drcrk;  // Дебет / Кредит
    protected Domain d_docnum;
    protected Domain d_budat;  // Дата документа
    protected Domain d_docdate;
    protected Domain d_docId;  // Ключ документа
    protected Domain d_hsl;
    protected Domain d_docsum;
    protected Domain d_vat;
    protected Domain d_quantity;

    protected Domain d_zuonr; //домейн контракт

    protected Domain d_date;      //дата 8 символов
    protected Domain d_gjahr;      //год 4 символов
    protected Domain d_zvat_rate;      //Ставка НДС
    protected Domain d_mwskz;     //Коды налогов
    protected Domain d_fistl;     // Службы/ПФМ
    protected Domain d_fipos;     // Финансовые позиции
    protected Domain d_zfi_cf015;  // Вид деятельности
    protected Domain d_belnr;      // Номер бухгалтерского документа
    protected Domain d_doclnd;     // Строка проводки по регистру
    protected Domain d_buzei;      // Номер отдельной позиции
    
    public void run() {
        // используем "построитель" БД метаинформации
        // создаем из шаблона по-умолчанию
        // будет включать базовые типы, БО пользователей и языков
        builder = MetaDbBuilder.createDemoDefault();
        // язык, на котором будут задаваться описания
        builder.setLangId("ru");

        // Создаем домейн для наименований бух. объектов
        d_name = builder.createDomain("ACCNAME", DataType.VARCHAR, "Наименование", t -> {
            t.setColName("name");    // имя колонки в таблице по-умолчание
            t.setVisible(true);      // видимость колонки в GUI по-умолчание, тут для демо по дефолту true
            t.setNote("Наименование"); // описание колонки в БД
        });
        d_hsl = builder.createDomain("AMOUNT", DataType.DECIMAL, "Сумма", t -> {
            t.setSize(23);
            t.setScale(2);
            t.setColName("hsl");
            t.setNote("Сумма");
        });
        d_docsum = builder.createDomain("DOCSUM", DataType.DECIMAL, "Сумма документа", t -> {
            t.setSize(13);
            t.setScale(2);
            t.setNote("Сумма документа");
        });
        
        d_date = builder.createDomain("SYMDATE", DataType.CHAR, "Дата", t -> {
            t.setSize(8);
            t.setNote("Дата");
        });
        d_gjahr = builder.createDomain("SYMYEAR", DataType.CHAR, "Год", t -> {
            t.setSize(4);
            t.setColName("gjahr");
            t.setNote("Год");
        });
        d_belnr = builder.createDomain("BELNR", DataType.CHAR, "ФД", t -> {
            t.setSize(10);
            t.setColName("belnr");
            t.setNote("Номер документа");
        });
        d_doclnd = builder.createDomain("DOCLND", DataType.CHAR, "ПФД", t -> {
            t.setSize(6);
            t.setColName("doclnd");
            t.setNote("Строка проводки по регистру");
        });
        d_budat = builder.createDomain("BUDAT", DataType.CHAR, "Дата док.", t -> {
            t.setSize(8);
            t.setColName("budat");      // имя колонки в таблице по-умолчание
            t.setNote("Дата документа");     // описание колонки в БД
        });

        // Добавляем источник данных, куда будем деплоить таблицы
        dataSource = builder.createDataSource("demo", t -> {
            t.setMajorVersion(17); // версия ПГ
        });

        currency = createCurrency();       // создаем БО валюта
        accountPlan = createAccountPlan(); // создаем БО план счетов и его таблицы
        balanceUnit = createBalanceUnit(); // создаем БО Балансовая единица
        account = createAccout();          // создаем БО счет - пример 2 таблицы в БО
        mapAccPlan = createMapAccPlan();   // Мэппинг счета и плана счетов
        docKind = createDocKind();         // Вид документа

        partner = createPartner();           // контрагент
        contract = createContract();         // контракт
        zvat = createZvat();             // Ставка НДС
        mwskz = createMwskz();              // Коды налогов
        pfm = createPfm();                  // Службы/ПФМ

        fipos = createFipos();              // Финансовые позиции
        costObject = createСostObject();   // Объект учета затрат
        zfiActivity = createZfiActivity();  // Вид деятельности
        register = createRegister();       // Регистр
        registerGroup = createRegisterGroup();  // Группа регистров
        mapRegGr = createMapRegGr();       // Мэппинг ГР и РЕГ

        entryKind = createEntryKind(); // Дебет / Кредит

        entryAbstract = createEntryAbstract(); // Проводки абстрактный

        createEntry6001020000();
        createEntry6002000000();
        createEntry1904010000();
        createEntry5100000201();
        createEntry3500340000();
        createEntry3500100000();
    
        createCorrItem();

//        saveMigration(1);
        var bouser = builder.getDb().getBusinessObjectViewer().findByName("User").get();
        // деплой юзеров
        builder.deploy(bouser, dataSource, DeployMode.PRIMARY);
    
        /*
        //mapDkAccount = createMapDkAccount();  // Мэппинг контрагента и БЕ

        //docum = createDocum();             // Документ
        //documPosAbstract = createDocumPosAbstract(); // Позиции документа абстрактный
        //outcomeItem = createOutcomeItem(); // Позиции документа расхода
        fiDocum = createFiDocum();

        //entry62v1 = createEntry62v1(); // Проводки 62 счет
        //entry90v1 = createEntry90v1(); // Проводки 90 счет

        entryAbstract = createEntryAbstract2(); // Проводки абстрактный
        createEntry1005040000();
        createEntry1011000000();
        createEntry3100110000();
        createEntry3100150000();

         */
        saveMigration(1);
    }

    private void saveMigration(int migration) {
        builder.setMigration(migration);
        String mFileName = "%s/metadata%s.json";
        String source = migration > 1 ? String.format(mFileName, workDir, migration - 1) : null;
        String target = mFileName.formatted(workDir, migration);
        // Сохранение метаданных
        saveToFile(target, builder.getDb().getJson(true));
        
        MigrationGenerator operationGen = MigrationFactory.fromFiles(source, target);
        var mdata = operationGen.generate(dataSource.getName());
        MigrationLoader.saveToFile(mdata, "%s/migration%s.json".formatted(workDir, migration), true);

        // Получение генератора миграций
//        SqlMigrationGen gen = SqlMigrationFactory.fromFiles(source, target);
        // генерация миграций для источника данных
//        SqlMigrationBunch migrations = gen.generate(dataSource.getName());
        // Транслятор миграций в формат Liquibase
//        LiquibaseTranslator translator = new LiquibaseTranslator(dataSource.getName(), "admin");
        // получение скрипта миграций в SQL формате Liquibase
//        String sql = translator.toSqlFormat(migrations);

//        String migFileName = "%s/migration%s.sql";
//        saveToFile(migFileName.formatted(workDir, migration), sql);
    }

    public void saveToFile(String fileName, String data) {
        try {
            Files.writeString(Paths.get(fileName),
                    data,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private Domain charDomain(String colName, int size, String dispName, String note) {
        return builder.createDomain(colName.toUpperCase()+size, DataType.CHAR, dispName, t -> {
            t.setSize(size);
            t.setColName(colName.toLowerCase());
            t.setNote(note);
        });
    }

    private TableEditor createAccountPlan() {
        // Создаем БО
        BusinessObject bo = builder.createBusinessObject("AccountPlan",
                "План счетов");
        // Указываем источник данных, где выполнять развертывание объекта
        builder.deploy(bo, dataSource, DeployMode.PRIMARY);

        // Создаем домейн (тип данных) кода плана счетов
        d_pacc = builder.createDomain("PACC", DataType.CHAR, "ПСЧ", t -> {
            t.setSize(4);             // размер типа
            t.setColName("pacc");    // имя колонки в таблице по-умолчание
            t.setNote("Код плана счетов"); // описание колонки в БД
        });

        // Создаем таблицу плана счетов
        TableEditor tab = builder.createTable(bo);
        // создаем колонку
        var id = builder.createCol(tab,
                d_pacc.getColName(), // имя поля
                null, // отображаемое имя, не задано, будет взято из домейна
                d_pacc, // домейн
                true // NOT NULL
        );
        var name = builder.createCol(tab, d_name, true);
        // основной ключ
        builder.setPrimaryKey(tab, id.getName());

        // указываем создавать внешний ключ в других таблицах
        // ссылающийся на таблицу план счетов
        // при новом использовании этого домейна
        d_pacc = builder.editDomain(d_pacc, t -> {
            t.setRefTabId(tab.getTabId());
            t.setRefTabKey(id.getId());
        });

        builder.appendRow(tab, id.getName(), "UTRU", name.getName(), "ПАО ЮТэйр");

        return tab;
    }

    private TableEditor createCurrency() {
        // Создаем БО
        BusinessObject bo = builder.createBusinessObject("Currency",
                "Валюта");
        // Указываем источник данных, где выполнять развертывание объекта
        builder.deploy(bo, dataSource, DeployMode.PRIMARY);

        // Создаем домейн (тип данных)
        d_cur = builder.createDomain("CUR", DataType.CHAR, "ВАЛ", t -> {
            t.setSize(5);
            t.setColName("cur");      // имя колонки в таблице по-умолчание
            t.setNote("Код валюты"); // описание колонки в БД
        });

        // Создаем таблицу
        TableEditor tab = builder.createTable(bo, t -> {
        });
        // создаем колонку
        var id = builder.createCol(tab, d_cur, true);
        var name = builder.createCol(tab, d_name, true);
        // основной ключ
        builder.setPrimaryKey(tab, id.getName());

        // указываем создавать внешний ключ в других таблицах
        d_cur = builder.editDomain(d_cur, t -> {
            t.setRefTabId(tab.getTabId());
            t.setRefTabKey(id.getId());
        });
/*
        // добавляем в таблицу данные (в таком случае данные нельзя редактировать в БД)
        builder.appendRow(tab, id.getName(), "AED", name.getName(), "AED");
        builder.appendRow(tab, id.getName(), "AMD", name.getName(), "AMD");
        builder.appendRow(tab, id.getName(), "AZN", name.getName(), "AZN");
        builder.appendRow(tab, id.getName(), "BYN", name.getName(), "BYN");
        builder.appendRow(tab, id.getName(), "CAD", name.getName(), "CAD");
        builder.appendRow(tab, id.getName(), "CHF", name.getName(), "CHF");
        builder.appendRow(tab, id.getName(), "CNY", name.getName(), "CNY");
        builder.appendRow(tab, id.getName(), "CZK", name.getName(), "CZK");
        builder.appendRow(tab, id.getName(), "DKK", name.getName(), "DKK");
        builder.appendRow(tab, id.getName(), "EUR", name.getName(), "Евро");
        builder.appendRow(tab, id.getName(), "EURUE", name.getName(), "EURUE");
        builder.appendRow(tab, id.getName(), "GBP", name.getName(), "GBP");
        builder.appendRow(tab, id.getName(), "INR", name.getName(), "INR");
        builder.appendRow(tab, id.getName(), "ISK", name.getName(), "ISK");
        builder.appendRow(tab, id.getName(), "JPY", name.getName(), "JPY");
        builder.appendRow(tab, id.getName(), "KGS", name.getName(), "KGS");
        builder.appendRow(tab, id.getName(), "KZT", name.getName(), "KZT");
        builder.appendRow(tab, id.getName(), "NOK", name.getName(), "NOK");
        builder.appendRow(tab, id.getName(), "PLN", name.getName(), "PLN");
        builder.appendRow(tab, id.getName(), "RUB", name.getName(), "Рубль");
        builder.appendRow(tab, id.getName(), "SEK", name.getName(), "SEK");
        builder.appendRow(tab, id.getName(), "TJS", name.getName(), "TJS");
        builder.appendRow(tab, id.getName(), "TRY", name.getName(), "TRY");
        builder.appendRow(tab, id.getName(), "USD", name.getName(), "Доллар");
        builder.appendRow(tab, id.getName(), "USDUE", name.getName(), "USDUE");
        builder.appendRow(tab, id.getName(), "UZS", name.getName(), "UZS");
        builder.appendRow(tab, id.getName(), "XDR", name.getName(), "XDR");
        builder.appendRow(tab, id.getName(), "XDRUE", name.getName(), "XDRUE");
        builder.appendRow(tab, id.getName(), "ZAR", name.getName(), "ZAR");
*/
        return tab;
    }

    private TableEditor createBalanceUnit() {
        // Создаем БО
        BusinessObject bo = builder.createBusinessObject("BalanceUnit",
                "Балансовая единица");
        // Указываем источник данных, где выполнять развертывание объекта
        builder.deploy(bo, dataSource, DeployMode.PRIMARY);

        // Создаем домейн
        d_bukrs = builder.createDomain("BALUNIT", DataType.CHAR, "БЕ", t -> {
            t.setSize(4);             // размер типа
            t.setColName("bukrs");    // имя колонки в таблице по-умолчание
            t.setNote("Код балансовой единицы"); // описание колонки в БД
        });

        // Создаем таблицу Балансовая единица
        TableEditor tab = builder.createTable(bo);
        // создаем колонку
        var id = builder.createCol(tab, d_bukrs, true);
        var name = builder.createCol(tab, d_name, true);
        Column ap = builder.createCol(tab, d_pacc, true); // внешний ключ план счетов
        tab.refreshFk(ap);         // строим ссылку на план работ

        Column curr = builder.createCol(tab, d_cur, true); // внешний ключ валюта
        tab.refreshFk(curr);         // строим ссылку на валюту

        builder.setPrimaryKey(tab, id.getName());

        // указываем создавать внешний ключ в других таблицах
        // ссылающийся на таблицу план счетов
        // при новом использовании этого домейна
        d_bukrs = builder.editDomain(d_bukrs, t -> {
            t.setRefTabId(tab.getTabId());
            t.setRefTabKey(id.getId());
        });

        return tab;
    }

    /*
        // Создаем домейн тип счета
        d_ktoks = builder.createDomain("ATYPE", DataType.CHAR, "Тип счета", t -> {
            t.setSize(4);             // размер типа
            t.setColName("ktoks");    // имя колонки в таблице по-умолчание
            t.setNote("Код типа счета"); // описание колонки в БД
        });

        // Создаем таблицу Тип счета
        var structure = builder.createStructure(bo, bo.getName() + "_type");
        TableEditor atab = builder.createTable(structure.getName(), bo, t -> {
            t.setNote("Тип счета");  // описание таблицы в БД
            t.setStructureId(structure.getId());
        });
        // создаем колонку
        var aid = builder.createCol(atab, d_ktoks, true);
        var aname = builder.createCol(atab, d_name, true);

        // добавляем в таблицу данные (в таком случае данные нельзя редактировать в БД)
        builder.appendRow(atab, aid.getName(), "UMAT", aname.getName(), "Запасные части");
        builder.appendRow(atab, aid.getName(), "UOSN", aname.getName(), "Хозяйственные материалы");
        builder.appendRow(atab, aid.getName(), "UZTR", aname.getName(), "Материальные затраты");

        atab.setPk(List.of(aid.getName()));

        // указываем создавать внешний ключ в других таблицах
        d_ktoks = builder.editDomain(d_ktoks, t -> {
            t.setRefTabId(atab.getTabId());
            t.setRefTabKey(aid.getId());
        });

        Column at = builder.createCol(tab, d_ktoks, true); // внешний ключ типа счета
        tab.refreshFk(at);         // строим ссылку на типа счета

     */
    private TableEditor createAccout() {
        // Создаем БО
        BusinessObject bo = builder.createBusinessObject("Account",
                "Счет");
        // Указываем источник данных, где выполнять развертывание объекта
        builder.deploy(bo, dataSource, DeployMode.PRIMARY);

        // Создаем домейн счет
        d_racct = builder.createDomain("ACCOUNT", DataType.CHAR, "СЧ", t -> {
            t.setSize(10);             // размер типа
            t.setColName("racct");    // имя колонки в таблице по-умолчание
            t.setNote("Код счета"); // описание колонки в БД
        });

        // Создаем таблицу счет
        TableEditor tab = builder.createTable(bo, t -> {
            t.setNote("Счет");  // описание таблицы в БД
        });
        // создаем колонку
        var id = builder.createCol(tab, d_racct, true);
        var name = builder.createCol(tab, d_name, true);

        var boname = builder.createCol(tab, "boname", "Бизнес объект", d_name, true);

        builder.setPrimaryKey(tab, id.getName());

        // указываем создавать внешний ключ в других таблицах
        d_racct = builder.editDomain(d_racct, t -> {
            t.setRefTabId(tab.getTabId());
            t.setRefTabKey(id.getId());
        });
/*
        builder.appendRow(tab,
                id.getName(), "6001020000",
                name.getName(), "РАСЧ С ПОСТАВЩИКАМИ И ПОДРЯДЧИКАМИ ЗА РАБОТЫ/УСЛУГ",
                boname.getName(), "entry_6001020000"
        );
        builder.appendRow(tab,
                id.getName(), "6002000000",
                name.getName(), "АВАНСЫ ВЫДАННЫЕ ПОСТАВЩИКАМ И ПОДРЯДЧИКАМ",
                boname.getName(), "entry_6002000000"
        );
        builder.appendRow(tab,
                id.getName(), "1904010000",
                name.getName(), "НДС ПО ПРИОБРЕТЕННЫМ РАБОТАМ, УСЛУГАМ",
                boname.getName(), "entry_1904010000"
        );
        builder.appendRow(tab,
                id.getName(), "5100000201",
                name.getName(), "Расчетный счет",
                boname.getName(), "entry_5100000201"
        );
        builder.appendRow(tab,
                id.getName(), "3500340000",
                name.getName(), "РАСХОДЫ НА ТРАНСПОРТНЫЕ УСЛУГИ",
                boname.getName(), "entry_3500340000"
        );
        builder.appendRow(tab,
                id.getName(), "3500100000",
                name.getName(), "АЭРОПОРТОВЫЕ РАСХОДЫ",
                boname.getName(), "entry_3500100000"
        );
        */
        return tab;
    }

    private TableEditor createMapAccPlan() {
        // Создаем БО
        BusinessObject bo = builder.createBusinessObject("MapAccplan",
                "Мэппинг счета и плана счетов");
        // Указываем источник данных, где выполнять развертывание объекта
        builder.deploy(bo, dataSource, DeployMode.PRIMARY);

        // Создаем таблицу Мэппинг счета и плана счетов
        TableEditor tab = builder.createTable(bo, t -> {
            t.setNote("Мэппинг счета и плана счетов");  // описание таблицы в БД
        });
        Column pacc = builder.createCol(tab, d_pacc, true); // внешний ключ план счетов
        tab.refreshFk(pacc);         // строим ссылку на план работ
        Column acc = builder.createCol(tab, d_racct, true); // внешний ключ счета
        tab.refreshFk(acc);         // строим ссылку на валюту

        builder.setPrimaryKey(tab, pacc.getName(), acc.getName());

        /*
        builder.appendRow(tab, acc.getName(), "6001020000", pacc.getName(), "UTRU");
        builder.appendRow(tab, acc.getName(), "6002000000", pacc.getName(), "UTRU");
        builder.appendRow(tab, acc.getName(), "1904010000", pacc.getName(), "UTRU");
        builder.appendRow(tab, acc.getName(), "5100000201", pacc.getName(), "UTRU");
        builder.appendRow(tab, acc.getName(), "3500340000", pacc.getName(), "UTRU");
        builder.appendRow(tab, acc.getName(), "3500100000", pacc.getName(), "UTRU");
*/
        return tab;
    }

    private TableEditor createDocKind() {
        // Создаем БО
        BusinessObject bo = builder.createBusinessObject("DocKind",
                "Вид документа");
        // Указываем источник данных, где выполнять развертывание объекта
        builder.deploy(bo, dataSource, DeployMode.PRIMARY);

        // Создаем домейн
        d_blart = builder.createDomain("DKIND", DataType.CHAR, "ВД", t -> {
            t.setSize(2);             // размер типа
            t.setColName("blart");    // имя колонки в таблице по-умолчание
            t.setNote("Код вида документа"); // описание колонки в БД
        });

        // Создаем таблицу
        TableEditor tab = builder.createTable(bo, t -> {
            t.setNote("Вид документа");  // описание таблицы в БД
        });
        // создаем колонку
        var id = builder.createCol(tab, d_blart, true);
        var name = builder.createCol(tab, d_name, true);
        //var boname = builder.createCol(tab, "boname", "Бизнес объект", d_name, true);

        builder.setPrimaryKey(tab, id.getName());

        // указываем создавать внешний ключ в других таблицах
        d_blart = builder.editDomain(d_blart, t -> {
            t.setRefTabId(tab.getTabId());
            t.setRefTabKey(id.getId());
        });
/*
        builder.appendRow(tab, id.getName(), "1D", name.getName(), "Перв.ввод остатк деб");
        builder.appendRow(tab, id.getName(), "1K", name.getName(), "Первон.ввод ост кред");
        builder.appendRow(tab, id.getName(), "2D", name.getName(), "Перв.ввод ост аван д");
        builder.appendRow(tab, id.getName(), "2K", name.getName(), "Перв.ввод ост аван к");
        builder.appendRow(tab, id.getName(), "AA", name.getName(), "Проводка по ОснСредс");
        builder.appendRow(tab, id.getName(), "AB", name.getName(), "Выравнивание");
        builder.appendRow(tab, id.getName(), "AC", name.getName(), "Выравнивание/ВозврАв");
        builder.appendRow(tab, id.getName(), "AD", name.getName(), "ЗачетАванса Дебитор");
        builder.appendRow(tab, id.getName(), "AF", name.getName(), "Проводка амортизации");
        builder.appendRow(tab, id.getName(), "AK", name.getName(), "ЗачетАванса Кредитор");
        builder.appendRow(tab, id.getName(), "AM", name.getName(), "Ведомость командиров");
        builder.appendRow(tab, id.getName(), "AO", name.getName(), "Авансовый отчет");
        builder.appendRow(tab, id.getName(), "BV", name.getName(), "Банковская выписка");
        builder.appendRow(tab, id.getName(), "CC", name.getName(), "Расч. заказов СО->FI");
        builder.appendRow(tab, id.getName(), "CO", name.getName(), "Расчет затрат");
        builder.appendRow(tab, id.getName(), "CP", name.getName(), "ПроизводствПоказател");
        builder.appendRow(tab, id.getName(), "D1", name.getName(), "Реализация ОС");
        builder.appendRow(tab, id.getName(), "D2", name.getName(), "Реализация НМА");
        builder.appendRow(tab, id.getName(), "D3", name.getName(), "Реализация НКС");
        builder.appendRow(tab, id.getName(), "DA", name.getName(), "Аванс Дебитора");
        builder.appendRow(tab, id.getName(), "DB", name.getName(), "Корректировка доход");
        builder.appendRow(tab, id.getName(), "DM", name.getName(), "Претенз. Дебитор");
        builder.appendRow(tab, id.getName(), "DR", name.getName(), "Счет-фактура FI Деб");
        builder.appendRow(tab, id.getName(), "DU", name.getName(), "Списание СрокИск");
        builder.appendRow(tab, id.getName(), "DV", name.getName(), "Возвр.платеж Дебитор");
        builder.appendRow(tab, id.getName(), "DW", name.getName(), "Возвр.аванса Дебитор");
        builder.appendRow(tab, id.getName(), "DY", name.getName(), "Проч расч FI Дебитор");
        builder.appendRow(tab, id.getName(), "DZ", name.getName(), "Платежи дебитора");
        builder.appendRow(tab, id.getName(), "FO", name.getName(), "Фин. обязательства");
        builder.appendRow(tab, id.getName(), "FR", name.getName(), "Финансовый результат");
        builder.appendRow(tab, id.getName(), "FV", name.getName(), "Фин. вложения");
        builder.appendRow(tab, id.getName(), "GE", name.getName(), "ГТД экспорт");
        builder.appendRow(tab, id.getName(), "GI", name.getName(), "ГТД импорт");
        builder.appendRow(tab, id.getName(), "KA", name.getName(), "Аванс кредитору");
        builder.appendRow(tab, id.getName(), "KB", name.getName(), "Корректировка расход");
        builder.appendRow(tab, id.getName(), "KG", name.getName(), "КредАвизо кредитора");
        builder.appendRow(tab, id.getName(), "KI", name.getName(), "НачисленияПоУслугам");
        builder.appendRow(tab, id.getName(), "KM", name.getName(), "Претенз. Кредитор");
        builder.appendRow(tab, id.getName(), "KP", name.getName(), "Ведение счета");
        builder.appendRow(tab, id.getName(), "KQ", name.getName(), "Прочие операции/кред");
        builder.appendRow(tab, id.getName(), "KR", name.getName(), "Счет-фактура FI Кред");
        builder.appendRow(tab, id.getName(), "KV", name.getName(), "Возвр.платежа Кредит");
        builder.appendRow(tab, id.getName(), "KW", name.getName(), "Возвр.аванса Кредит");
        builder.appendRow(tab, id.getName(), "KY", name.getName(), "Проч расч FI Кредит");
        builder.appendRow(tab, id.getName(), "KZ", name.getName(), "Платеж кредитору");
        builder.appendRow(tab, id.getName(), "L1", name.getName(), "Реформация баланса");
        builder.appendRow(tab, id.getName(), "ML", name.getName(), "РегистрМат:  расчет");
        builder.appendRow(tab, id.getName(), "NA", name.getName(), "Начисление налоги");
        builder.appendRow(tab, id.getName(), "NU", name.getName(), "Налоговая коррект-ка");
        builder.appendRow(tab, id.getName(), "PK", name.getName(), "Переоценка ОП");
        builder.appendRow(tab, id.getName(), "PR", name.getName(), "Изменение цены");
        builder.appendRow(tab, id.getName(), "RA", name.getName(), "Недопоставки АТИ");
        builder.appendRow(tab, id.getName(), "RB", name.getName(), "Документ ОСч Кр стор");
        builder.appendRow(tab, id.getName(), "RC", name.getName(), "Перевыставл Сбыт");
        builder.appendRow(tab, id.getName(), "RD", name.getName(), "РезСомнитДолг");
        builder.appendRow(tab, id.getName(), "RE", name.getName(), "Счет-фактура MM");
        builder.appendRow(tab, id.getName(), "RM", name.getName(), "Регистр материала");
        builder.appendRow(tab, id.getName(), "RN", name.getName(), "Накладная MM");
        builder.appendRow(tab, id.getName(), "RV", name.getName(), "Счет-фактура Сбыт");
        builder.appendRow(tab, id.getName(), "SA", name.getName(), "Документ ОСч");
        builder.appendRow(tab, id.getName(), "SB", name.getName(), "Документ переноса");
        builder.appendRow(tab, id.getName(), "SF", name.getName(), "Первон.ввод сальдо");
        builder.appendRow(tab, id.getName(), "SH", name.getName(), "Перенос затрат в ОУР");
        builder.appendRow(tab, id.getName(), "SI", name.getName(), "РБП (страхование)");
        builder.appendRow(tab, id.getName(), "SR", name.getName(), "Док по резервам (КС)");
        builder.appendRow(tab, id.getName(), "SS", name.getName(), "Сторно по ОснСчету");
        builder.appendRow(tab, id.getName(), "ST", name.getName(), "Документ сторно");
        builder.appendRow(tab, id.getName(), "SZ", name.getName(), "Платеж по осн.счету");
        builder.appendRow(tab, id.getName(), "T1", name.getName(), "Технический ТАП");
        builder.appendRow(tab, id.getName(), "T2", name.getName(), "Технический ТАП");
        builder.appendRow(tab, id.getName(), "T3", name.getName(), "Технический ТП");
        builder.appendRow(tab, id.getName(), "TD", name.getName(), "ТАП дебитора");
        builder.appendRow(tab, id.getName(), "TI", name.getName(), "Билеты реестр");
        builder.appendRow(tab, id.getName(), "TK", name.getName(), "ТАП кредитора");
        builder.appendRow(tab, id.getName(), "TZ", name.getName(), "Документ TRM");
        builder.appendRow(tab, id.getName(), "VB", name.getName(), "Возврат билетов");
        builder.appendRow(tab, id.getName(), "VZ", name.getName(), "Взаимозачет");
        builder.appendRow(tab, id.getName(), "WA", name.getName(), "Выдача материалов");
        builder.appendRow(tab, id.getName(), "WB", name.getName(), "ПоступлУслугиДос");
        builder.appendRow(tab, id.getName(), "WE", name.getName(), "ПоступлМатериала");
        builder.appendRow(tab, id.getName(), "WI", name.getName(), "ДокумИнвентаризации");
        builder.appendRow(tab, id.getName(), "WL", name.getName(), "ОтпускМат/поставка");
        builder.appendRow(tab, id.getName(), "WN", name.getName(), "ПоступленМатер нетто");
        builder.appendRow(tab, id.getName(), "YA", name.getName(), "Аванс Авиаперевозки");
        builder.appendRow(tab, id.getName(), "YI", name.getName(), "Исправления Авиапер");
        builder.appendRow(tab, id.getName(), "YK", name.getName(), "Корректир. Авиапер");
        builder.appendRow(tab, id.getName(), "YR", name.getName(), "Реализация Авиаперев");
        builder.appendRow(tab, id.getName(), "YS", name.getName(), "Аванс Авиаперев/Возв");
        builder.appendRow(tab, id.getName(), "YV", name.getName(), "ПрочееРасходВыручки");
        builder.appendRow(tab, id.getName(), "YX", name.getName(), "ЗачетАвансАвиа/Возв");
        builder.appendRow(tab, id.getName(), "YZ", name.getName(), "Зачет аванса Авипер");
        builder.appendRow(tab, id.getName(), "ZP", name.getName(), "Интеграция с зарплат");
        builder.appendRow(tab, id.getName(), "ZR", name.getName(), "ЗНО взаимозачет");
*/
        return tab;
    }

    /*
    private TableEditor createMapDkAccount() {
        // Создаем БО
        BusinessObject bo = builder.createBusinessObject("map_agent_account",
                "Мэппинг контрагента и БЕ");
        // Указываем источник данных, где выполнять развертывание объекта
        // НЕ ДЕПЛОИМ
        //builder.deploy(bo, dataSource, DeployMode.PRIMARY);

        // Создаем таблицу счет
        TableEditor tab = builder.createTable(bo.getName(), bo);
        Column dk = builder.createCol(tab, d_agent, true); // внешний ключ контрагента
        tab.refreshFk(dk);         // строим ссылку на контрагента
        Column bu = builder.createCol(tab, d_bukrs, true); // код балансовой единицы
        tab.refreshFk(bu);         // строим ссылку на балансовую единицу
        Column acc = builder.createCol(tab, d_racct, true); // код счета
        tab.refreshFk(acc);         // строим ссылку на счет
        Column ck = builder.createCol(tab, d_ck, true); // вид контрагента
        tab.refreshFk(ck);         // строим ссылку на вид контрагента

        tab.setPk(List.of(dk.getName(), bu.getName(), acc.getName(), ck.getName()));

        return tab;

    }
     */
    private TableEditor createDocum() {
        // Создаем БО
        BusinessObject bo = builder.createBusinessObject("Docum",
                "Документ");
        // Указываем источник данных, где выполнять развертывание объекта
        // не деплоим
        //builder.deploy(bo, dataSource, DeployMode.PRIMARY);

        d_docnum = builder.createDomain("DOCNUM", DataType.VARCHAR, "Номер док.", t -> {
            t.setSize(50);             // размер типа
            t.setColName("doc_num");      // имя колонки в таблице по-умолчание
            t.setNote("Номер документа");     // описание колонки в БД
        });

        d_docdate = builder.createDomain("DOCDATE", DataType.DATE, "Дата док.", t -> {
            t.setColName("doc_date");      // имя колонки в таблице по-умолчание
            t.setNote("Дата документа");     // описание колонки в БД
        });

        d_docId = builder.createDomain("DocumentId", DataType.UUID, "Ключ док.", t -> {
            t.setColName("doc_id");      // имя колонки в таблице по-умолчание
            t.setNote("Ключ документа");     // описание колонки в БД
        });

        // Создаем таблицу
        TableEditor tab = builder.createTable(bo);
        // создаем колонку
        var id = builder.createCol(tab, d_docId, true, t -> {
            var dv = new DefValueDto();
            dv.setType(DvType.UUID);
            t.setDefValue(dv);
        });
        d_docId = builder.editDomain(d_docId, t -> {
            t.setRefTabId(tab.getTabId());
            t.setRefTabKey(id.getId());
        });
        var dkind = builder.createCol(tab, d_blart, true);
        tab.refreshFk(dkind);
        var cur = builder.createCol(tab, d_cur, true);
        tab.refreshFk(cur);
        builder.createCol(tab, d_belnr, true);
        builder.createCol(tab, d_budat, true);

        builder.setPrimaryKey(tab, id.getName());

        return tab;

    }

    private TableEditor createDocumPosAbstract() {
        // Создаем БО
        BusinessObject bo = builder.createBusinessObject("DocumPosAbstract",
                "Позиция документа", t -> {
                    t.setAbstractObject(true);
                });

        // Создаем таблицу
        TableEditor tab = builder.createTable(bo);
        // создаем колонку
        var d_uuidgen = builder.getDomain("UUIDKey");
        var id = builder.createCol(tab, "id", "Ключ", d_uuidgen, true);
        builder.setPrimaryKey(tab, id.getName());

        var d_int = builder.getDomain("INTEGER");
        builder.createCol(tab, "num", "Поз.", d_int, true, t -> {
            t.setNote("Номер позиции");
        });
        var docId = builder.createCol(tab, d_docId, true);
        tab.refreshFk(docId);

        return tab;
    }

    private TableEditor createOutcomeItem() {
        // Создаем БО
        BusinessObject bo = builder.createBusinessObject("OutcomeItem",
                "Позиция расходного документа");
        // НЕ ДЕПЛОИМ
        //builder.deploy(bo, dataSource, DeployMode.PRIMARY);

        // Создаем таблицу
        TableEditor tab = builder.createTable(bo);//, documPosAbstract);

        var agent = builder.createCol(tab, d_vptnr, true);
        tab.refreshFk(agent);

        builder.createCol(tab, "good_name", "Наименование изделия", d_name, true);
        builder.createCol(tab, "unit_name", "Ед.изм.", d_name, true, t -> {
            t.setNote("Единица измерения");
        });

        d_quantity = builder.createDomain("QUANTITY", DataType.DECIMAL, "Кол.", t -> {
            t.setSize(16);
            t.setScale(6);
            t.setColName("quantity");
            t.setNote("Количество");
        });
        builder.createCol(tab, d_quantity, true);

        builder.createCol(tab, "amount", "Сумма без НДС", d_hsl, true, t -> {
            t.setNote("Сумма без НДС");
        });

        d_vat = builder.createDomain("VAT", DataType.DECIMAL, "НДС", t -> {
            t.setSize(16);
            t.setScale(2);
            t.setColName("vat");
            t.setNote("НДС");
        });

        builder.createCol(tab, d_vat, true);

        builder.createCol(tab, "total", "Сумма с НДС", d_hsl, true, t -> {
            t.setNote("Сумма с НДС");
        });

        return tab;

    }

    private TableEditor createFiDocum() {

        BusinessObject bo = builder.createBusinessObject("Fidocum",
                "Финансовый Документ");
        builder.deploy(bo, dataSource, DeployMode.PRIMARY);

        // Создаем таблицу
        TableEditor tab = builder.createTable(bo);
        // создаем колонку
        var id = builder.createCol(tab, d_belnr, true);
        d_belnr = builder.editDomain(d_belnr, t -> {
            t.setRefTabId(tab.getTabId());
            t.setRefTabKey(id.getId());
        });
        builder.createCol(tab, d_budat, true);
        var bukrs = builder.createCol(tab, d_bukrs, true);
        tab.refreshFk(bukrs);

        var ldgrp = builder.createCol(tab, d_ldgrp, true);
        tab.refreshFk(ldgrp);

        var waers = builder.createCol(tab, d_cur, true);
        tab.refreshFk(waers);

        var blart = builder.createCol(tab, d_blart, true);
        tab.refreshFk(blart);

        builder.setPrimaryKey(tab, id.getName());

        return tab;

    }

    private TableEditor createEntry62v1() {
        // Создаем БО
        BusinessObject bo = builder.createBusinessObject("entry_6200000001",
                "Проводки 62 счет", t -> {
                    t.setVersioning(true);
                });
        //builder.deploy(bo, dataSource, DeployMode.PRIMARY);

        // Создаем таблицу
        TableEditor tab = builder.createTable(bo);//, entryAbstract);

        var agent = builder.createCol(tab, d_vptnr, true);
        tab.refreshFk(agent);

        builder.createFk(tab, List.of("dpos_id"), outcomeItem, outcomeItem.getPkColNames());

        return tab;

    }

    private TableEditor createEntry90v1() {
        // Создаем БО
        BusinessObject bo = builder.createBusinessObject("entry_9000000001",
                "Проводки 90 счет", t -> {
                    t.setVersioning(true);
                });
        //builder.deploy(bo, dataSource, DeployMode.PRIMARY);

        // Создаем таблицу
        TableEditor tab = builder.createTable(bo);//, entryAbstract);

        return tab;
    }

    private TableEditor createEntryAbstract2() {
        // Создаем БО
        BusinessObject bo = builder.createBusinessObject("entry_abstract",
                "Бухгалтерская проводка", t -> {
                    t.setAbstractObject(true);
                });

        // Создаем таблицу
        TableEditor tab = builder.createTable(bo);

        Domain d_docln = builder.createDomain("DOCLN", DataType.CHAR, "Поз.док.", t -> {
            t.setSize(6);             // размер типа
            t.setColName("docln");    // имя колонки в таблице по-умолчание
            t.setNote("Позиция документа"); // описание колонки в БД
        });

        Domain d_gjahr = builder.createDomain("GJAHR", DataType.CHAR, "GJAHR", t -> {
            t.setSize(4);
            t.setColName("gjahr");
            t.setNote("GJAHR");
        });

        var rldnr = builder.createCol(tab, d_rldnr, true);
        tab.refreshFk(rldnr);

        var bukrs = builder.createCol(tab, d_bukrs, true);
        tab.refreshFk(bukrs);

        var gjahr = builder.createCol(tab, d_gjahr, true);

        var belnr = builder.createCol(tab, d_belnr, true);
        tab.refreshFk(belnr);

        var docln = builder.createCol(tab, d_docln, true);

        var drcrk = builder.createCol(tab, d_drcrk, true);
        tab.refreshFk(drcrk);

        var racct = builder.createCol(tab, d_racct, true);
        tab.refreshFk(racct);

        builder.createCol(tab, d_hsl, true);

        builder.createCol(tab, d_budat, true);

        var blart = builder.createCol(tab, d_blart, true);
        tab.refreshFk(blart);

        var waers = builder.createCol(tab, d_cur, true);
        tab.refreshFk(waers);

        builder.setPrimaryKey(tab, rldnr.getName(), bukrs.getName(), gjahr.getName(),
                belnr.getName(), docln.getName());

        return tab;

    }

    private TableEditor createEntry1005040000() {
        // Создаем БО
        BusinessObject bo = builder.createBusinessObject("entry_1005040000",
                "Проводки счет 1005040000", t -> {
                    t.setVersioning(true);
                });
        builder.deploy(bo, dataSource, DeployMode.PRIMARY);

        //TableEditor tab = builder.createTableInherited(bo.getName() + "_1", bo, entryAbstract);

        //return tab;
        return null;
    }

    private TableEditor createEntry1011000000() {
        // Создаем БО
        BusinessObject bo = builder.createBusinessObject("entry_1011000000",
                "Проводки счет 1011000000", t -> {
                    t.setVersioning(true);
                });
        builder.deploy(bo, dataSource, DeployMode.PRIMARY);

        //TableEditor tab = builder.createTableInherited(bo.getName() + "_1", bo, entryAbstract);

        //return tab;
        return null;
    }

    private TableEditor createEntry3100110000() {
        // Создаем БО
        BusinessObject bo = builder.createBusinessObject("entry_3100110000",
                "Проводки счет 3100110000", t -> {
                    t.setVersioning(true);
                });
        builder.deploy(bo, dataSource, DeployMode.PRIMARY);

        //TableEditor tab = builder.createTableInherited(bo.getName() + "_1", bo, entryAbstract);

        //var drcrk = builder.createCol(tab, "rcntr", null, d_kostl, true);
        //tab.refreshFk(drcrk);

        //return tab;
        return null;
    }

    private TableEditor createEntry3100150000() {
        // Создаем БО
        BusinessObject bo = builder.createBusinessObject("entry_3100150000",
                "Проводки счет 3100150000", t -> {
                    t.setVersioning(true);
                });
        builder.deploy(bo, dataSource, DeployMode.PRIMARY);

        //TableEditor tab = builder.createTableInherited(bo.getName() + "_1", bo, entryAbstract);

        //var drcrk = builder.createCol(tab, "rcntr", null, d_kostl, true);
        //tab.refreshFk(drcrk);

        //return tab;
        return null;
    }

    private TableEditor createPartner() {
        // Создаем БО
        BusinessObject bo = builder.createBusinessObject("Partner",
                "Контрагент");

        builder.deploy(bo, dataSource, DeployMode.PRIMARY);

        // Создаем домейн контрагент
        d_vptnr = builder.createDomain("VPTNR", DataType.CHAR, "ДК", t -> {
            t.setSize(10);          // размер типа
            t.setColName("vptnr");    // имя колонки в таблице по-умолчание
            t.setNote("Код контрагента"); // описание колонки в БД
        });

        TableEditor tab = builder.createTable(bo);
        // создаем колонку
        var id = builder.createCol(tab, d_vptnr, true);
        builder.createCol(tab, d_name, true);

        builder.setPrimaryKey(tab, id.getName());

        d_vptnr = builder.editDomain(d_vptnr, t -> {
            t.setRefTabId(tab.getTabId());
            t.setRefTabKey(id.getId());
        });

        return tab;

    }

    private TableEditor createContract() {
        // Создаем БО
        BusinessObject bo = builder.createBusinessObject("Contract",
                "Договор");

        builder.deploy(bo, dataSource, DeployMode.PRIMARY);

        d_zuonr = builder.createDomain("ZUONR", DataType.CHAR, "ДОГ", t -> {
            t.setSize(18);
            t.setColName("zuonr");
            t.setNote("Код договора");
        });

        TableEditor tab = builder.createTable(bo);
        // создаем колонку
        var id = builder.createCol(tab, d_zuonr, true);
        builder.createCol(tab, "ZUONR_INT_NUMBER".toLowerCase(), "Номер", d_name, false);
        builder.createCol(tab, "ZUONR_EXT_DATE".toLowerCase(), null, d_date, false);
        var partner = builder.createCol(tab, d_vptnr, false);
        tab.refreshFk(partner);

        builder.setPrimaryKey(tab, id.getName());

        d_zuonr = builder.editDomain(d_zuonr, t -> {
            t.setRefTabId(tab.getTabId());
            t.setRefTabKey(id.getId());
        });

        return tab;

    }

    private TableEditor createZvat() {

        BusinessObject bo = builder.createBusinessObject("Zvat",
                "Ставка НДС");

        builder.deploy(bo, dataSource, DeployMode.PRIMARY);

        d_zvat_rate = builder.createDomain("ZVAT_RATE", DataType.CHAR, "НДС", t -> {
            t.setSize(2);
            t.setColName("ZVAT_RATE".toLowerCase());
            t.setNote("Код ставки НДС");
        });

        TableEditor tab = builder.createTable(bo);

        var id = builder.createCol(tab, d_zvat_rate, true);
        var txt = builder.createCol(tab, "ZVAT_TEXT".toLowerCase(), "Ставка НДС", d_name, true);

        builder.setPrimaryKey(tab, id.getName());

        // указываем создавать внешний ключ в других таблицах
        d_zvat_rate = builder.editDomain(d_zvat_rate, t -> {
            t.setRefTabId(tab.getTabId());
            t.setRefTabKey(id.getId());
        });

        return tab;

    }

    private TableEditor createMwskz() {

        BusinessObject bo = builder.createBusinessObject("Mwskz",
                "Коды налогов");

        builder.deploy(bo, dataSource, DeployMode.PRIMARY);

        d_mwskz = builder.createDomain("MWSKZ", DataType.CHAR, "НЛГ", t -> {
            t.setSize(2);
            t.setColName("mwskz");
            t.setNote("Код налога");
        });

        TableEditor tab = builder.createTable(bo);

        var id = builder.createCol(tab, d_mwskz, true);
        builder.createCol(tab, "TEXT1".toLowerCase(), "Описание", d_name, true);

        builder.setPrimaryKey(tab, id.getName());

        d_mwskz = builder.editDomain(d_mwskz, t -> {
            t.setRefTabId(tab.getTabId());
            t.setRefTabKey(id.getId());
        });

        return tab;

    }

    private TableEditor createPfm() {

        BusinessObject bo = builder.createBusinessObject("Pfm",
                "Службы/ПФМ");

        builder.deploy(bo, dataSource, DeployMode.PRIMARY);

        d_fistl = builder.createDomain("FISTL", DataType.VARCHAR, "ПФМ", t -> {
            t.setSize(16);
            t.setColName("FISTL".toLowerCase());
            t.setNote("Код ПФМ");
        });

        TableEditor tab = builder.createTable(bo);

        var id = builder.createCol(tab, d_fistl, true);
        builder.createCol(tab, "N_FISTL".toLowerCase(), "Описание", d_name, true);

        builder.setPrimaryKey(tab, id.getName());

        d_fistl = builder.editDomain(d_fistl, t -> {
            t.setRefTabId(tab.getTabId());
            t.setRefTabKey(id.getId());
        });

        return tab;

    }

    private TableEditor createFipos() {

        BusinessObject bo = builder.createBusinessObject("Fipos",
                "Финансовые позиции");

        builder.deploy(bo, dataSource, DeployMode.PRIMARY);

        d_fipos = builder.createDomain("fipos".toUpperCase(), DataType.VARCHAR, "ФП", t -> {
            t.setSize(14);
            t.setColName("fipos");
            t.setNote("Код финансовой позиции");
        });

        TableEditor tab = builder.createTable(bo);

        var id = builder.createCol(tab, d_fipos, true);
        builder.createCol(tab, "N_FIPOS".toLowerCase(), "Описание", d_name, false);
        builder.createCol(tab, "GJAHR".toLowerCase(), "Год", d_gjahr, false);

        builder.setPrimaryKey(tab, id.getName());

        d_fipos = builder.editDomain(d_fipos, t -> {
            t.setRefTabId(tab.getTabId());
            t.setRefTabKey(id.getId());
        });

        return tab;

    }

    private TableEditor createСostObject() {
        // Создаем БО
        BusinessObject bo = builder.createBusinessObject("CostObject",
                "Объект учета затрат");
        // Указываем источник данных, где выполнять развертывание объекта
        builder.deploy(bo, dataSource, DeployMode.PRIMARY);

        // Создаем домейн ОУЗ
        d_kostl = builder.createDomain("KOSTL", DataType.CHAR, "ОУЗ", t -> {
            t.setSize(10);          // размер типа
            t.setColName("kostl");    // имя колонки в таблице по-умолчание
            t.setNote("Код объекта учета затрат (ОУЗ)"); // описание колонки в БД
        });

        // Создаем таблицу ОУЗ
        TableEditor tab = builder.createTable(bo);
        // создаем колонку
        var id = builder.createCol(tab, d_kostl, true);
        var name = builder.createCol(tab, "ktext", "Название", d_name, false);
        var bu = builder.createCol(tab, d_bukrs, false);
        tab.refreshFk(bu);

        builder.setPrimaryKey(tab, id.getName());

        // указываем создавать внешний ключ в других таблицах
        d_kostl = builder.editDomain(d_kostl, t -> {
            t.setRefTabId(tab.getTabId());
            t.setRefTabKey(id.getId());
        });

        return tab;
    }

    private TableEditor createZfiActivity() {

        BusinessObject bo = builder.createBusinessObject("ZfiActivity",
                "Вид деятельности");

        builder.deploy(bo, dataSource, DeployMode.PRIMARY);

        d_zfi_cf015 = builder.createDomain("ZFI_CF015", DataType.CHAR, "ВД", t -> {
            t.setSize(2);
            t.setColName("zfi_cf015");
            t.setNote("Код вида деятельности");
        });

        TableEditor tab = builder.createTable(bo);

        var id = builder.createCol(tab, d_zfi_cf015, true);
        var name = builder.createCol(tab, "N_ZFI_CF015".toLowerCase(), "Описание", d_name, true);

        builder.setPrimaryKey(tab, id.getName());

        d_zfi_cf015 = builder.editDomain(d_zfi_cf015, t -> {
            t.setRefTabId(tab.getTabId());
            t.setRefTabKey(id.getId());
        });

        return tab;

    }

    private TableEditor createRegister() {
        // Создаем БО
        BusinessObject bo = builder.createBusinessObject("Register",
                "Регистр");
        // Указываем источник данных, где выполнять развертывание объекта
        builder.deploy(bo, dataSource, DeployMode.PRIMARY);

        d_rldnr = builder.createDomain("REG", DataType.CHAR, "РЕГ", t -> {
            t.setSize(2);             // размер типа
            t.setColName("rldnr");      // имя колонки в таблице по-умолчание
            t.setNote("Код регистра");     // описание колонки в БД
        });

        // Создаем таблицу
        TableEditor tab = builder.createTable(bo);
        // создаем колонку
        var id = builder.createCol(tab, d_rldnr, true);
        var name = builder.createCol(tab, d_name, true);
        // основной ключ
        builder.setPrimaryKey(tab, id.getName());

        // указываем создавать внешний ключ в других таблицах
        d_rldnr = builder.editDomain(d_rldnr, t -> {
            t.setRefTabId(tab.getTabId());
            t.setRefTabKey(id.getId());
        });

        return tab;

    }

    private TableEditor createRegisterGroup() {
        // Создаем БО
        BusinessObject bo = builder.createBusinessObject("RegisterGroup",
                "Группа регистров");
        // Указываем источник данных, где выполнять развертывание объекта
        builder.deploy(bo, dataSource, DeployMode.PRIMARY);

        // Создаем домейн (тип данных)
        d_ldgrp = builder.createDomain("GREG", DataType.CHAR, "ГР", t -> {
            t.setSize(4);             // размер типа
            t.setColName("ldgrp");      // имя колонки в таблице по-умолчание
            t.setNote("Код группы регистров");     // описание колонки в БД
        });

        // Создаем таблицу
        TableEditor tab = builder.createTable(bo);
        // создаем колонку
        var id = builder.createCol(tab, d_ldgrp, true);
        var name = builder.createCol(tab, d_name, true);
        // основной ключ
        builder.setPrimaryKey(tab, id.getName());

        // указываем создавать внешний ключ в других таблицах
        d_ldgrp = builder.editDomain(d_ldgrp, t -> {
            t.setRefTabId(tab.getTabId());
            t.setRefTabKey(id.getId());
        });

        return tab;

    }

    private TableEditor createMapRegGr() {
        // Создаем БО
        BusinessObject bo = builder.createBusinessObject("MapReggr",
                "Мэппинг групп регистра и регистров с БЕ");
        // Указываем источник данных, где выполнять развертывание объекта
        builder.deploy(bo, dataSource, DeployMode.PRIMARY);

        // ppСоздаем таблицу Мэппинг групп регистра и регистров с БЕ
        TableEditor tab = builder.createTable(bo);
        Column gr = builder.createCol(tab, d_ldgrp, true); // внешний ключ группы
        tab.refreshFk(gr);         // строим ссылку на группы
        Column reg = builder.createCol(tab, d_rldnr, true); // внешний ключ регистр
        tab.refreshFk(reg);         // строим ссылку на регистр
        Column bu = builder.createCol(tab, d_bukrs, true); // внешний ключ БЕ
        tab.refreshFk(bu);         // строим ссылку на БЕ

        builder.setPrimaryKey(tab, gr.getName(), reg.getName(), bu.getName());

        return tab;

    }

    private TableEditor createEntryKind() {
        // Создаем БО
        BusinessObject bo = builder.createBusinessObject("EntryKind",
                "Тип проводки");
        builder.deploy(bo, dataSource, DeployMode.PRIMARY);

        // Создаем домейн Дебет / Кредит
        d_drcrk = builder.createDomain("EKIND", DataType.CHAR, "Д/К", t -> {
            t.setSize(1);             // размер типа
            t.setColName("drcrk");    // имя колонки в таблице по-умолчание
            t.setNote("Дебет / Кредит"); // описание колонки в БД
        });
        // Создаем таблицу Тип счета
        TableEditor tab = builder.createTable(bo, t -> {
            t.setNote("Дебет / Кредит");  // описание таблицы в БД
        });
        // создаем колонку
        var id = builder.createCol(tab, d_drcrk, true);
        var name = builder.createCol(tab, d_name, true);

        d_drcrk = builder.editDomain(d_drcrk, t -> {
            t.setRefTabId(tab.getTabId());
            t.setRefTabKey(id.getId());
        });

        // добавляем в таблицу данные (в таком случае данные нельзя редактировать в БД)
        //Дебет = S, кредит = H.
        //У счетов 10* - нет аналитик в этом примере. У счетов 3* - одинаковая аналитика - МВЗ (ACDOCA-RCNTR)
        builder.appendRow(tab, id.getName(), "S", name.getName(), "Дебет");
        builder.appendRow(tab, id.getName(), "H", name.getName(), "Кредит");

        builder.setPrimaryKey(tab, id.getName());

        return tab;

    }

    private TableEditor createEntryAbstract() {
        // Создаем БО
        BusinessObject bo = builder.createBusinessObject("Entry",
                "Бухгалтерская проводка", t -> {
                    t.setAbstractObject(true);
                });

        // Создаем таблицу
        TableEditor tab = builder.createTable(bo);
        // создаем колонку

        var rldnr = builder.createCol(tab, d_rldnr, true);
        tab.refreshFk(rldnr);
        var bukrs = builder.createCol(tab, d_bukrs, true);
        tab.refreshFk(bukrs);
        var gjahr = builder.createCol(tab, d_gjahr, true);
        var belnr = builder.createCol(tab, d_belnr, true);
        var doclnd = builder.createCol(tab, d_doclnd, true);
        var ekind = builder.createCol(tab, d_drcrk, true);
        tab.refreshFk(ekind);
        var accountch = builder.createCol(tab, d_racct, true);
        tab.refreshFk(accountch);
        builder.createCol(tab, d_hsl, true);
        builder.createCol(tab, d_budat, true);
        var blart = builder.createCol(tab, d_blart, true);
        tab.refreshFk(blart);

        var cur = builder.createCol(tab, "rhcur", null, d_cur, true);
        tab.refreshFk(cur);
        
        d_buzei = builder.createDomain("buzei".toUpperCase(), DataType.CHAR, "НОП", t -> {
            t.setSize(3);             
            t.setColName("buzei");
            t.setNote("Номер отдельной позиции");
        });
       
        builder.createCol(tab, d_buzei, true);

        builder.setPrimaryKey(tab, rldnr.getName(), bukrs.getName(), gjahr.getName(), belnr.getName(), doclnd.getName());

        return tab;

    }

    private void createCorrItem() {

        BusinessObject bo = builder.createBusinessObject("CorrItem",
                "Позиции строк корреспонденции счетов");

        builder.deploy(bo, dataSource, DeployMode.PRIMARY);
        
        TableEditor tab = builder.createTable(bo);
        
        var bukrs = builder.createCol(tab, d_bukrs, true);
        tab.refreshFk(bukrs);
        
        var belnr = builder.createCol(tab, d_belnr, true);
        var gjahr = builder.createCol(tab, d_gjahr, true);
        
        var buzeid = builder.createCol(tab, "buzeid", "Номер отдельной позиции (дебет)", d_buzei, true);
        var buzeik = builder.createCol(tab, "buzeik", "Номер отдельной позиции (кредит)", d_buzei, true);
        var addd = builder.createCol(tab, charDomain("addd", 3, "НПНПД", "Номер позиции для новых позиций (дебет)"), true);
        var addk = builder.createCol(tab, charDomain("addk", 3, "НПНПК", "Номер позиции для новых позиций (кредит)"), true);
        var npddi = builder.createCol(tab, charDomain("npddi", 9, "НПРСД", "Номер позиции из разделенной статьи (дебет)"), true);
        var npdki = builder.createCol(tab, charDomain("npdki", 9, "НПРСК", "Номер позиции из разделенной статьи (кредит)"), true);
        
        builder.createCol(tab, "debet", "Счет дебетования", d_racct, true);
        builder.createCol(tab, charDomain("xnegpd", 1, "ИД:КС", "Индикатор: красное сторно"), true);
        builder.createCol(tab, "kredit", "Кредитовый счет", d_racct, true);
        builder.createCol(tab, charDomain("xnegpk", 1, "ИК:КС", "Индикатор: красное сторно"), true);
        builder.createCol(tab, "dmbtr", "Сумма во внутренней валюте", d_docsum, true);
        builder.createCol(tab, "hwaer", "Внутренняя валюта", d_cur, true);
        builder.createCol(tab, "wrbtr", "Сумма в валюте документа", d_docsum, true);
        builder.createCol(tab, "waers", "Код валюты", d_cur, true);
        
        builder.createCol(tab, "doclnd", "Строка проводки по регистру, 6 знаков", d_doclnd, true);
        builder.createCol(tab, "doclnk", "Строка проводки по регистру, 6 знаков", d_doclnd, true);
        
        var rldnr = builder.createCol(tab, d_rldnr, true);
        tab.refreshFk(rldnr);

    }

    private TableEditor createEntry6001020000() {

        BusinessObject entry = builder.getBusinessObject("Entry");
        BusinessObject bo = builder.createBusinessObject("Entry6001020000",
                "Проводки счет 6001020000", t -> {
                    t.setVersioning(true);
                    t.setInheritedId(entry.getId());
                });
        
        builder.deploy(bo, dataSource, DeployMode.PRIMARY);

        //TableEditor tab = builder.createTableInherited(bo.getName() + "_1", bo, entryAbstract);
        TableEditor tab = builder.editTable(bo.getName() + "_1");

        //Вид деятельности	ВД	ZFI_CF015	CHAR(2)
        var zfi_cf015 = builder.createCol(tab, d_zfi_cf015, true);
        tab.refreshFk(zfi_cf015);
        //Контрагент	ДК	VPTNR	CHAR(10)
        var vptnr = builder.createCol(tab, d_vptnr, true);
        tab.refreshFk(vptnr);
        //Договор	ДОГ	ZUONR
        var zuonr = builder.createCol(tab, d_zuonr, true);
        tab.refreshFk(zuonr);
        //Ставка НДС	НДС	ZVAT_RATE	VARCHAR(2)
        var zvat_rate = builder.createCol(tab, d_zvat_rate, true);
        tab.refreshFk(zvat_rate);

        return tab;
    }

    private TableEditor createEntry6002000000() {

        BusinessObject entry = builder.getBusinessObject("Entry");
        BusinessObject bo = builder.createBusinessObject("Entry6002000000",
                "Проводки счет 6002000000", t -> {
                    t.setVersioning(true);
                    t.setInheritedId(entry.getId());
                });
        builder.deploy(bo, dataSource, DeployMode.PRIMARY);

        //TableEditor tab = builder.createTableInherited(bo.getName() + "_1", bo, entryAbstract);
        TableEditor tab = builder.editTable(bo.getName() + "_1");

        //Вид деятельности	ВД	ZFI_CF015	CHAR(2)
        var zfi_cf015 = builder.createCol(tab, d_zfi_cf015, true);
        tab.refreshFk(zfi_cf015);
        //Контрагент	ДК	VPTNR	CHAR(10)
        var vptnr = builder.createCol(tab, d_vptnr, true);
        tab.refreshFk(vptnr);
        //Договор	ДОГ	ZUONR
        var zuonr = builder.createCol(tab, d_zuonr, true);
        tab.refreshFk(zuonr);
        //Ставка НДС	НДС	ZVAT_RATE	VARCHAR(2)
        var zvat_rate = builder.createCol(tab, d_zvat_rate, true);
        tab.refreshFk(zvat_rate);

        return tab;
    }

    private TableEditor createEntry1904010000() {

        BusinessObject entry = builder.getBusinessObject("Entry");
        BusinessObject bo = builder.createBusinessObject("Entry1904010000",
                "Проводки счет 1904010000", t -> {
                    t.setVersioning(true);
                    t.setInheritedId(entry.getId());
                });
        builder.deploy(bo, dataSource, DeployMode.PRIMARY);

        //TableEditor tab = builder.createTableInherited(bo.getName() + "_1", bo, entryAbstract);
        TableEditor tab = builder.editTable(bo.getName() + "_1");

        //Вид деятельности	ВД	ZFI_CF015	CHAR(2)
        var zfi_cf015 = builder.createCol(tab, d_zfi_cf015, true);
        tab.refreshFk(zfi_cf015);
        //Ставка НДС	НДС	ZVAT_RATE	VARCHAR(2)
        var zvat_rate = builder.createCol(tab, d_zvat_rate, true);
        tab.refreshFk(zvat_rate);
        //Код налога	НЛГ	MWSKZ
        var col_mwskz = builder.createCol(tab, d_mwskz, true);
        tab.refreshFk(col_mwskz);

        return tab;
    }

    private TableEditor createEntry5100000201() {

        BusinessObject entry = builder.getBusinessObject("Entry");
        BusinessObject bo = builder.createBusinessObject("Entry5100000201",
                "Проводки счет 5100000201", t -> {
                    t.setVersioning(true);
                    t.setInheritedId(entry.getId());
                });
        builder.deploy(bo, dataSource, DeployMode.PRIMARY);

        //TableEditor tab = builder.createTableInherited(bo.getName() + "_1", bo, entryAbstract);
        TableEditor tab = builder.editTable(bo.getName() + "_1");

        //Вид деятельности	ВД	ZFI_CF015	CHAR(2)
        var zfi_cf015 = builder.createCol(tab, d_zfi_cf015, true);
        tab.refreshFk(zfi_cf015);

        //ПФМ	ПФМ	FISTL
        var col_fistl = builder.createCol(tab, d_fistl, true);
        tab.refreshFk(col_fistl);

        //Фин.позиция	ФП	FIPOS
        var col_fipos = builder.createCol(tab, d_fipos, true);
        tab.refreshFk(col_fipos);

        //Ставка НДС	НДС	ZVAT_RATE	VARCHAR(2)
        var zvat_rate = builder.createCol(tab, d_zvat_rate, true);
        tab.refreshFk(zvat_rate);

        return tab;
    }

    private TableEditor createEntry3500340000() {

        BusinessObject entry = builder.getBusinessObject("Entry");
        BusinessObject bo = builder.createBusinessObject("Entry3500340000",
                "Проводки счет 3500340000", t -> {
                    t.setVersioning(true);
                    t.setInheritedId(entry.getId());
                });
        builder.deploy(bo, dataSource, DeployMode.PRIMARY);

        //TableEditor tab = builder.createTableInherited(bo.getName() + "_1", bo, entryAbstract);
        TableEditor tab = builder.editTable(bo.getName() + "_1");

        //Вид деятельности	ВД	ZFI_CF015	CHAR(2)
        var zfi_cf015 = builder.createCol(tab, d_zfi_cf015, true);
        tab.refreshFk(zfi_cf015);

        //Объект учета	ОУЗ	KOSTL
        var col_kostl = builder.createCol(tab, d_kostl, true);
        tab.refreshFk(col_kostl);

        return tab;
    }

    private TableEditor createEntry3500100000() {

        BusinessObject entry = builder.getBusinessObject("Entry");
        BusinessObject bo = builder.createBusinessObject("Entry3500100000",
                "Проводки счет 3500100000", t -> {
                    t.setVersioning(true);
                    t.setInheritedId(entry.getId());
                });
        builder.deploy(bo, dataSource, DeployMode.PRIMARY);

        //TableEditor tab = builder.createTableInherited(bo.getName() + "_1", bo, entryAbstract);
        TableEditor tab = builder.editTable(bo.getName() + "_1");

        //Вид деятельности	ВД	ZFI_CF015	CHAR(2)
        var zfi_cf015 = builder.createCol(tab, d_zfi_cf015, true);
        tab.refreshFk(zfi_cf015);

        //Объект учета	ОУЗ	KOSTL
        var col_kostl = builder.createCol(tab, d_kostl, true);
        tab.refreshFk(col_kostl);

        return tab;
    }

}
