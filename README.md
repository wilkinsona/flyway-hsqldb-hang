### Flyway migration hang with HSQLDB

To reproduce the problem:

```bash
$ ./mvnw exec:java
[INFO] Scanning for projects...
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] Building flyway-hsqldb-hang 0.0.1-SNAPSHOT
[INFO] ------------------------------------------------------------------------
[INFO]
[INFO] --- exec-maven-plugin:1.5.0:java (default-cli) @ flyway-hsqldb-hang ---
HSQLDB transaction manager: org.hsqldb.TransactionManager2PL@424adac4
Jul 15, 2016 9:39:53 AM org.flywaydb.core.internal.util.VersionPrinter printVersion
INFO: Flyway 3.2.1 by Boxfuse
Jul 15, 2016 9:39:53 AM org.flywaydb.core.internal.dbsupport.DbSupportFactory createDbSupport
INFO: Database: jdbc:hsqldb:mem:hang (HSQL Database Engine 2.3)
Jul 15, 2016 9:39:53 AM org.flywaydb.core.internal.command.DbValidate validate
INFO: Validated 1 migration (execution time 00:00.014s)
Jul 15, 2016 9:39:53 AM org.flywaydb.core.internal.metadatatable.MetaDataTableImpl createIfNotExists
INFO: Creating Metadata table: "PUBLIC"."schema_version"
Jul 15, 2016 9:39:53 AM org.flywaydb.core.internal.command.DbMigrate migrate
INFO: Current version of schema "PUBLIC": << Empty Schema >>
Jul 15, 2016 9:39:53 AM org.flywaydb.core.internal.command.DbMigrate applyMigration
INFO: Migrating schema "PUBLIC" to version 1 - init
```

It has [been suggested][1] that setting `hsqldb.tx` to `mvcc` will work around the problem.
However, this does not appear to work:

```bash
$ ./mvnw exec:java -Dmvcc=true
[INFO] Scanning for projects...
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] Building flyway-hsqldb-hang 0.0.1-SNAPSHOT
[INFO] ------------------------------------------------------------------------
[INFO]
[INFO] --- exec-maven-plugin:1.5.0:java (default-cli) @ flyway-hsqldb-hang ---
HSQLDB transaction manager: org.hsqldb.TransactionManagerMVCC@424adac4
Jul 15, 2016 9:40:35 AM org.flywaydb.core.internal.util.VersionPrinter printVersion
INFO: Flyway 3.2.1 by Boxfuse
Jul 15, 2016 9:40:35 AM org.flywaydb.core.internal.dbsupport.DbSupportFactory createDbSupport
INFO: Database: jdbc:hsqldb:mem:hang (HSQL Database Engine 2.3)
Jul 15, 2016 9:40:35 AM org.flywaydb.core.internal.command.DbValidate validate
INFO: Validated 1 migration (execution time 00:00.014s)
Jul 15, 2016 9:40:35 AM org.flywaydb.core.internal.metadatatable.MetaDataTableImpl createIfNotExists
INFO: Creating Metadata table: "PUBLIC"."schema_version"
Jul 15, 2016 9:40:35 AM org.flywaydb.core.internal.command.DbMigrate migrate
INFO: Current version of schema "PUBLIC": << Empty Schema >>
Jul 15, 2016 9:40:35 AM org.flywaydb.core.internal.command.DbMigrate applyMigration
INFO: Migrating schema "PUBLIC" to version 1 - init
```

The hang does not occur with HSQLDB 2.3.3:

```bash
$ ./mvnw exec:java -Dhsqldb.version=2.3.3
[INFO] Scanning for projects...
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] Building flyway-hsqldb-hang 0.0.1-SNAPSHOT
[INFO] ------------------------------------------------------------------------
[INFO]
[INFO] --- exec-maven-plugin:1.5.0:java (default-cli) @ flyway-hsqldb-hang ---
HSQLDB transaction manager: org.hsqldb.TransactionManager2PL@3c38eff8
Jul 15, 2016 9:41:02 AM org.flywaydb.core.internal.util.VersionPrinter printVersion
INFO: Flyway 3.2.1 by Boxfuse
Jul 15, 2016 9:41:02 AM org.flywaydb.core.internal.dbsupport.DbSupportFactory createDbSupport
INFO: Database: jdbc:hsqldb:mem:hang (HSQL Database Engine 2.3)
Jul 15, 2016 9:41:02 AM org.flywaydb.core.internal.command.DbValidate validate
INFO: Validated 1 migration (execution time 00:00.014s)
Jul 15, 2016 9:41:02 AM org.flywaydb.core.internal.metadatatable.MetaDataTableImpl createIfNotExists
INFO: Creating Metadata table: "PUBLIC"."schema_version"
Jul 15, 2016 9:41:02 AM org.flywaydb.core.internal.command.DbMigrate migrate
INFO: Current version of schema "PUBLIC": << Empty Schema >>
Jul 15, 2016 9:41:02 AM org.flywaydb.core.internal.command.DbMigrate applyMigration
INFO: Migrating schema "PUBLIC" to version 1 - init
Jul 15, 2016 9:41:02 AM org.flywaydb.core.internal.command.DbMigrate logSummary
INFO: Successfully applied 1 migration to schema "PUBLIC" (execution time 00:00.029s).
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 0.755 s
[INFO] Finished at: 2016-07-15T09:41:02+01:00
[INFO] Final Memory: 11M/309M
[INFO] ------------------------------------------------------------------------
```

However, 2.3.3 does hang when configured to use MVCC:

```bash
$ ./mvnw exec:java -Dmvcc=true -Dhsqldb.version=2.3.3
[INFO] Scanning for projects...
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] Building flyway-hsqldb-hang 0.0.1-SNAPSHOT
[INFO] ------------------------------------------------------------------------
[INFO]
[INFO] --- exec-maven-plugin:1.5.0:java (default-cli) @ flyway-hsqldb-hang ---
HSQLDB transaction manager: org.hsqldb.TransactionManagerMVCC@511bc697
Jul 15, 2016 9:42:05 AM org.flywaydb.core.internal.util.VersionPrinter printVersion
INFO: Flyway 3.2.1 by Boxfuse
Jul 15, 2016 9:42:05 AM org.flywaydb.core.internal.dbsupport.DbSupportFactory createDbSupport
INFO: Database: jdbc:hsqldb:mem:hang (HSQL Database Engine 2.3)
Jul 15, 2016 9:42:05 AM org.flywaydb.core.internal.command.DbValidate validate
INFO: Validated 1 migration (execution time 00:00.014s)
Jul 15, 2016 9:42:05 AM org.flywaydb.core.internal.metadatatable.MetaDataTableImpl createIfNotExists
INFO: Creating Metadata table: "PUBLIC"."schema_version"
Jul 15, 2016 9:42:05 AM org.flywaydb.core.internal.command.DbMigrate migrate
INFO: Current version of schema "PUBLIC": << Empty Schema >>
Jul 15, 2016 9:42:05 AM org.flywaydb.core.internal.command.DbMigrate applyMigration
INFO: Migrating schema "PUBLIC" to version 1 - init
```

[1]: http://stackoverflow.com/a/38281814/1384297