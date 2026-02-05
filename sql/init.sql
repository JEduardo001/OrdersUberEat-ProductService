INSERT INTO product_table (id, name, description, stock, create_at, deleted_at)
VALUES (
           '550e8400-e29b-41d4-a716-446655440100',
           'Hamburguesa Clásica',
           'Carne de res, queso, lechuga y tomate',
           100,
           CURRENT_TIMESTAMP,
           NULL
       )
    ON CONFLICT (id) DO NOTHING;