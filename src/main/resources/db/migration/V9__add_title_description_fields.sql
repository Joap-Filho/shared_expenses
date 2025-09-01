-- Add title field and make description optional
-- Rename existing description column to title and add new optional description

ALTER TABLE expense 
    RENAME COLUMN description TO title;

ALTER TABLE expense 
    ADD COLUMN description TEXT;

-- Update recurring_expense table as well
ALTER TABLE recurring_expense 
    RENAME COLUMN description TO title;

ALTER TABLE recurring_expense 
    ADD COLUMN description TEXT;

-- Add comment for clarity
COMMENT ON COLUMN expense.title IS 'Short title for the expense (required)';
COMMENT ON COLUMN expense.description IS 'Optional detailed description';
COMMENT ON COLUMN recurring_expense.title IS 'Short title for the recurring expense (required)';
COMMENT ON COLUMN recurring_expense.description IS 'Optional detailed description';
