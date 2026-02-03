create or replace TYPE ConversionRateRec AS OBJECT (
    currency VARCHAR2(3),
    reval_rate NUMBER,
    conversion_unit NUMBER
);
