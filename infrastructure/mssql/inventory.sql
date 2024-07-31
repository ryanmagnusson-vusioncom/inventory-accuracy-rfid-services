-- Create schema
EXEC sp_executesql N'CREATE SCHEMA [vtransmitv2]'


CREATE SEQUENCE vtransmitv2_rma.epc_readings_sequence
    START WITH 1
    INCREMENT BY 1;

CREATE SEQUENCE vtransmitv2_rma.sensors_sequence
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE [vtransmitv2].[vt2_lock]
(
    LOCK_KEY     CHAR(256)    NOT NULL,
    REGION       VARCHAR(128) NOT NULL,
    CLIENT_ID    CHAR(36),
    CREATED_DATE datetime     NOT NULL,
    constraint VT2_LOCK_PK primary key (LOCK_KEY, REGION)
    );

    CREATE TABLE [vtransmitv2_rma].[epc_readings] (
        [id] bigint NOT NULL,
        [reading_timestamp] [datetime] NOT NULL,
        [store_id] [varchar](255) NOT NULL,
        [sensor_mac_address] [varchar](30) NOT NULL,
        [correlation_id] [varchar](256) NOT NULL,
        [creation_date] [datetime] NOT NULL,
        [modification_date] [datetime] NOT NULL,

        [epc_data] [varchar](512) NULL,
        [epc_gtin] [varchar](100) NULL,
        [item_upc] [varchar](30) NULL,
        [epc_serial] [bigint] NULL,
        [epc_rssi] [int] NULL,
        CONSTRAINT [epc_reads_pk] PRIMARY KEY CLUSTERED ([id]),
        CONSTRAINT [epc_reads_unique_key] UNIQUE (reading_timestamp, store_id, sensor_mac_address, epc_data)
    );

CREATE NONCLUSTERED INDEX[epc_reads_idx_item_upc] ON [vtransmitv2_rma].[epc_readings] (item_upc,epc_serial);
CREATE NONCLUSTERED INDEX[epc_reads_idx_ts] ON [vtransmitv2_rma].[epc_readings] (reading_timestamp,store_id);
CREATE NONCLUSTERED INDEX[epc_reads_idx_store_sensor] ON [vtransmitv2_rma].[epc_readings] (sensor_mac_address, store_id);
CREATE NONCLUSTERED INDEX[epc_reads_idx_store_id] ON [vtransmitv2_rma].[epc_readings] (store_id);


/*
IF NOT EXISTS (SELECT name FROM sys.indexes WHERE object_id = OBJECT_ID('vtransmitv2.task') AND name = 'TASK_IDX_AVAILABLE_AT_LABEL_ID_PAGE')
    BEGIN
        CREATE NONCLUSTERED INDEX [TASK_IDX_AVAILABLE_AT_LABEL_ID_PAGE] ON [vtransmitv2].[task] ([task_type]) INCLUDE ([available_at], [label_id], [page], [priority], [status], [store_id]) WITH (ONLINE = ON);
    END
*/    


