ALTER TABLE sales
    ADD CONSTRAINT fk_sales_vehicle
        FOREIGN KEY (vehicle_id)
            REFERENCES vehicles(id);