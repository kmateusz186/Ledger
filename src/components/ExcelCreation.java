package components;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

import org.apache.poi.xssf.usermodel.*;

public class ExcelCreation {

	private final String file_path;

	private final int sheet_index;

	private String save_path;

	private String sheet_name;
	private String next_sheet_name;

	private final Map<String, Object> placeholders;

	public ExcelCreation(String file_path, int sheet_index, String save_path, String sheet_name,
			Map<String, Object> placeholders, String next_sheet_name) {
		this.file_path = file_path;
		this.sheet_index = sheet_index;
		this.save_path = save_path;
		this.sheet_name = sheet_name;
		this.placeholders = placeholders;
		this.next_sheet_name = next_sheet_name;
	}

	public boolean doExcel() {
		try (FileInputStream file = new FileInputStream(new File(file_path))) {
			// Load the template file
			final XSSFWorkbook wb = new XSSFWorkbook(file);
			int index;
			XSSFSheet sheet = wb.getSheetAt(sheet_index);
			XSSFRow row;
			XSSFCell cell;
			XSSFWorkbook wb_new;
			String filename_exist = save_path;
			File file1 = new File(filename_exist);
			String filename_new = System.getenv("userprofile") + "/Desktop/out1.xlsx";

			if (file1.exists()) {
				wb_new = new XSSFWorkbook(file1);
				index = wb_new.getSheetIndex(next_sheet_name);
				if (index != -1) {
					wb_new.removeSheetAt(index);
				}
				save_path = filename_new;
				sheet_name = next_sheet_name;
			} else {
				wb_new = new XSSFWorkbook();
			}

			int row_index = 0;
			int cell_index = 0;
			String cell_str_value = "";

			while (true) {
				row = sheet.getRow(row_index);

				if (row == null) {
					row = sheet.createRow(row_index++);
				}

				for (cell_index = 0; cell_index < row.getPhysicalNumberOfCells(); cell_index++) {
					cell = row.getCell(cell_index);
					cell_str_value = cell.toString();

					if (isPlaceholderValueMap(cell_str_value)) {
						cell.setCellValue(getValue(cell_str_value));
					}

					cell_str_value = sheet.getRow(row_index).getCell(0).toString();
				}

				if (isEndingCell(cell_str_value)) {
					row.getCell(0).setCellValue("");
					break;
				}
				row_index++;
			}
			wb_new = JXLSUtil.mergeExcelFiles(wb_new, wb, sheet_name);
			file.close();
			if (file1.exists()) {
				return doSaveExistedExcelFile(wb_new, file1, filename_new);
			} else {
				return doSaveNewExcelFile(wb_new);
			}
		} catch (IOException e) {
			System.out.println("[ERROR] " + e.getLocalizedMessage());
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
			return false;
		}
	}

	private boolean isPlaceholderValueMap(String cell) {
		return !cell.isEmpty() && cell.charAt(0) == '$';
	}

	private boolean isEndingCell(String cell) {
		return cell.equalsIgnoreCase("#end");
	}

	private String getValue(String cell) {
		if (placeholders.containsKey(cell.substring(1))) {
			return (String) placeholders.get(cell.substring(1));
		} else {
			return "";
		}
	}

	private boolean doSaveNewExcelFile(XSSFWorkbook wb) {
		try (FileOutputStream writeFile = new FileOutputStream(save_path)) {
			wb.write(writeFile);
			writeFile.flush();
			writeFile.close();
			return true;
		} catch (IOException e) {
			System.out.println("[ERROR] Encountered an error while saving the file.\n" + e.getLocalizedMessage());
			return false;
		}
	}

	private boolean doSaveExistedExcelFile(XSSFWorkbook wb, File file, String filename_new) {
		File file_new = new File(filename_new);
		try (FileOutputStream writeFile = new FileOutputStream(save_path)) {
			wb.write(writeFile);
			writeFile.flush();
			wb.close();
			writeFile.close();
			Files.copy(file_new.toPath(), file.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
			file_new.delete();
			return true;
		} catch (IOException e) {
			System.out.println("[ERROR] Encountered an error while saving the file.\n" + e.getLocalizedMessage());
			return false;
		}
	}
}