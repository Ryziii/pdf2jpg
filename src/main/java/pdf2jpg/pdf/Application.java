package pdf2jpg.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.ImageIOUtil;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Application {
	public static int convertedPages = 0;

	public static void main(String[] args) throws IOException {


		Scanner reader = new Scanner(System.in);
		System.out.println("***Pdf to JPG***");
		System.out.println("Enter foldername:");
		String path = reader.nextLine();
		reader.close();
		File file = new File(path);
		File[] files = file.listFiles();
		List fileList = Arrays.asList(files);
		Collections.sort(fileList, new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {

				if (o1.isDirectory() && o2.isFile())
					return -1;
				if (o1.isFile() && o2.isDirectory())
					return 1;
//				return o1.getName().compareTo(o2.getName());
				if(o1.getName().endsWith("pdf") && o2.getName().endsWith("pdf")) {
					ArrayList<Integer> res1 = new ArrayList<Integer>();
					ArrayList<Integer> res2 = new ArrayList<Integer>();
					int len1 = 0, len2 = 0;
					Matcher matcher1 = Pattern.compile("\\d+").matcher(o1.getName());
					Matcher matcher2 = Pattern.compile("\\d+").matcher(o2.getName());
					while (matcher1.find()) {
						res1.add(Integer.valueOf(matcher1.group()));
					}
					while (matcher2.find()) {
						res2.add(Integer.valueOf(matcher2.group()));
					}

					if (res1.get(0) == res2.get(0)) {
						if (res1.get(1) == res2.get(1)) {
							return res1.get(2).compareTo(res2.get(2));
						}
						return res1.get(1).compareTo(res2.get(1));
					}
					return res1.get(0).compareTo(res2.get(0));
				}
				return o1.getName().compareTo(o2.getName());
			}
		});
		for(File f : files){
			if(f.getName().endsWith("pdf")) {
//				System.out.println(f.getName());
				System.out.println("Konvertiere " + f.getName());
				new Application().convertToImg(f);
			}
		}
	}


	public void convertToImg(File file) throws IOException{

		PDDocument document = PDDocument.loadNonSeq(file, null);
		List<PDPage> pdPages = document.getDocumentCatalog().getAllPages();

		int page = 0;
		for (PDPage pdPage : pdPages) {
			++page;
			System.out.println(page + "/" + document.getNumberOfPages());
			BufferedImage bim = pdPage.convertToImage(BufferedImage.TYPE_INT_RGB, 200);
//			ImageIOUtil.writeImage(bim, file.getName() + "-" + page + ".jpg", 300);
			String parentName = file.getParent();
			String outdirName = parentName.substring(parentName.lastIndexOf("/")+1,parentName.length());
			File outdir = new File(file.getParentFile(), "out" + outdirName);
			if(!outdir.exists()){
				outdir.mkdir();
			}
			ImageIOUtil.writeImage(bim, outdir.getParent()+"/"+outdir.getName()+"/"+Application.convertedPages + ".jpg", 300);

			Application.convertedPages++;
		}
		document.close();

	}

}
