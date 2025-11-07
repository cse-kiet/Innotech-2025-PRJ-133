const OCR_API_URL = 'https://api.ocr.space/parse/image';
const OCR_API_KEY = 'K86696700788957';

// --- 1. Regex Definitions (based on Python reference) ---
const DATE_REGEX = /(^|[^0-9\w])(([0-9]{1,2}\s*[/\-\.]\s*){1,2}([0-9]{4}|[0-9]{2}))\b/g;
const MONTH_NAMES = "(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec|January|February|March|April|May|June|July|August|September|October|November|December)";
const MONTH_DATE_REGEX = new RegExp(`\\b(${MONTH_NAMES}[\\s\\-\\.]*(?:([0-9]{1,2})[\\s\\-\\.]*)?([0-9]{4}|[0-9]{2}))\\b`, 'gi');

// --- 2. Date Parsing Function (based on Python reference) ---
const parseDateString = (s) => {
  s = s.trim().replace(/-/g, '/').replace(/\./g, '/');

  const formats = ["%m/%y", "%m/%Y", "%d/%m/%y", "%m/%d/%y", "%d/%m/%Y", "%m/%d/%Y", "%Y/%m/%d"];

  for (const fmt of formats) {
    try {
      let date;
      if (fmt === "%m/%y" || fmt === "%m/%Y") {
        // MM/YYYY or MM/YY format
        const parts = s.split('/');
        if (parts.length === 2) {
          const month = parseInt(parts[0]) - 1; // JS months are 0-based
          let year = parseInt(parts[1]);
          if (year < 100) year += 2000; // Convert 2-digit year
          date = new Date(year, month, 1);
        }
      } else if (fmt === "%d/%m/%y" || fmt === "%d/%m/%Y") {
        const parts = s.split('/');
        if (parts.length === 3) {
          const day = parseInt(parts[0]);
          const month = parseInt(parts[1]) - 1;
          let year = parseInt(parts[2]);
          if (year < 100) year += 2000;
          date = new Date(year, month, day);
        }
      } else if (fmt === "%m/%d/%y" || fmt === "%m/%d/%Y") {
        const parts = s.split('/');
        if (parts.length === 3) {
          const month = parseInt(parts[0]) - 1;
          const day = parseInt(parts[1]);
          let year = parseInt(parts[2]);
          if (year < 100) year += 2000;
          date = new Date(year, month, day);
        }
      } else if (fmt === "%Y/%m/%d") {
        const parts = s.split('/');
        if (parts.length === 3) {
          const year = parseInt(parts[0]);
          const month = parseInt(parts[1]) - 1;
          const day = parseInt(parts[2]);
          date = new Date(year, month, day);
        }
      }

      if (date && !isNaN(date.getTime())) {
        return date;
      }
    } catch (e) {
      // Continue to next format
    }
  }

  // Try month name formats
  s = s.replace(/\//g, ' ');
  const monthNameFormats = [
    { regex: /^([A-Za-z]+)\s+(\d{2})$/, yearIndex: 2 },
    { regex: /^([A-Za-z]+)\s+(\d{4})$/, yearIndex: 2 }
  ];

  for (const fmt of monthNameFormats) {
    const match = s.match(fmt.regex);
    if (match) {
      const monthName = match[1].toLowerCase();
      const year = parseInt(match[fmt.yearIndex]);

      const monthIndex = [
        'jan', 'feb', 'mar', 'apr', 'may', 'jun',
        'jul', 'aug', 'sep', 'oct', 'nov', 'dec'
      ].indexOf(monthName.substring(0, 3));

      if (monthIndex !== -1) {
        const date = new Date(year, monthIndex, 1);
        if (!isNaN(date.getTime())) {
          return date;
        }
      }
    }
  }

  return null;
};

// Function to extract expiry date from OCR text (improved based on Python reference)
const extractExpiryDate = (text) => {
  console.log('[OCR] Extracting expiry date from text:', text);

  const datesFound = [];
  const allText = text;

  console.log('[OCR] All text found:', allText);

  // 1. Check for NUMERIC dates (based on Python regex)
  let match;
  while ((match = DATE_REGEX.exec(allText)) !== null) {
    const dateStrWithSpaces = match[2];
    const dateStr = dateStrWithSpaces.replace(/\s+/g, '');
    const parsedDate = parseDateString(dateStr);
    if (parsedDate && parsedDate.getFullYear() >= 2020 && parsedDate.getFullYear() <= 2030) {
      datesFound.push({
        text: dateStr,
        date: parsedDate,
        type: 'numeric'
      });
    }
  }

  // 2. Check for TEXT dates (month names)
  while ((match = MONTH_DATE_REGEX.exec(allText)) !== null) {
    const dateStr = match[1];
    const parsedDate = parseDateString(dateStr);
    if (parsedDate && parsedDate.getFullYear() >= 2020 && parsedDate.getFullYear() <= 2030) {
      datesFound.push({
        text: dateStr,
        date: parsedDate,
        type: 'text'
      });
    }
  }

  console.log('[OCR] Detected dates:', datesFound.map(d => `${d.text} (${d.type})`));

  if (datesFound.length === 0) {
    console.log('[OCR] No valid expiry dates found');
    return null;
  }

  // Remove duplicates and find the date with highest year
  const uniqueDates = datesFound.filter((date, index, self) =>
    index === self.findIndex(d => d.date.getTime() === date.date.getTime())
  );

  // Sort by year descending, then by full date descending
  uniqueDates.sort((a, b) => {
    const yearA = a.date.getFullYear();
    const yearB = b.date.getFullYear();

    if (yearA !== yearB) {
      return yearB - yearA; // Higher year first
    }

    // If same year, use full date comparison
    return b.date.getTime() - a.date.getTime();
  });

  const chosen = uniqueDates[0];

  console.log(`[OCR] Found ${uniqueDates.length} unique date(s). Choosing date with highest year: ${chosen.text}`);
  return chosen.date.toISOString().split('T')[0]; // Return YYYY-MM-DD format
};

// Function to preprocess image (client-side equivalent of Python preprocessing)
const preprocessImage = async (imageDataUrl) => {
  return new Promise((resolve) => {
    const img = new Image();
    img.onload = () => {
      const canvas = document.createElement('canvas');
      const ctx = canvas.getContext('2d');

      // Scale up by 3x (like Python version)
      const scale = 3;
      canvas.width = img.width * scale;
      canvas.height = img.height * scale;

      // Draw and scale
      ctx.drawImage(img, 0, 0, canvas.width, canvas.height);

      // Convert to grayscale
      const imageData = ctx.getImageData(0, 0, canvas.width, canvas.height);
      const data = imageData.data;

      for (let i = 0; i < data.length; i += 4) {
        const gray = data[i] * 0.299 + data[i + 1] * 0.587 + data[i + 2] * 0.114;
        data[i] = gray;     // Red
        data[i + 1] = gray; // Green
        data[i + 2] = gray; // Blue
        // Alpha remains unchanged
      }

      ctx.putImageData(imageData, 0, 0);

      // Apply gentle contrast enhancement (1.3x like Python)
      const contrastImageData = ctx.getImageData(0, 0, canvas.width, canvas.height);
      const contrastData = contrastImageData.data;
      const contrast = 1.3;
      const intercept = 128 * (1 - contrast);

      for (let i = 0; i < contrastData.length; i += 4) {
        contrastData[i] = contrastData[i] * contrast + intercept;
        contrastData[i + 1] = contrastData[i + 1] * contrast + intercept;
        contrastData[i + 2] = contrastData[i + 2] * contrast + intercept;
      }

      ctx.putImageData(contrastImageData, 0, 0);

      // Convert back to data URL
      const processedDataUrl = canvas.toDataURL('image/jpeg', 0.9);
      resolve(processedDataUrl);
    };
    img.src = imageDataUrl;
  });
};

// Function to perform OCR on an image with fallback attempts
export const performOCR = async (imageDataUrl) => {
  try {
    console.log('[OCR] Starting OCR process');

    // --- ATTEMPT 1: Try the ORIGINAL image first ---
    console.log('[OCR] Attempt 1: Processing original image');
    let currentImageData = imageDataUrl;
    let result = await performOCROnImage(currentImageData);

    if (result.expiryDate) {
      console.log('[OCR] Success on original image');
      return result;
    }

    // --- ATTEMPT 2: If nothing found, try PREPROCESSED image ---
    console.log('[OCR] No date found on original. Attempt 2: Processing preprocessed image');
    currentImageData = await preprocessImage(imageDataUrl);
    result = await performOCROnImage(currentImageData);

    console.log('[OCR] Final result:', result);
    return result;

  } catch (error) {
    console.error('[OCR] Error:', error);
    throw error;
  }
};

// Helper function to perform OCR on a single image
const performOCROnImage = async (imageDataUrl) => {
  // Convert data URL to blob
  const response = await fetch(imageDataUrl);
  const blob = await response.blob();

  // Create form data
  const formData = new FormData();
  formData.append('file', blob, 'image.jpg');
  formData.append('apikey', OCR_API_KEY);
  formData.append('language', 'eng');
  formData.append('isOverlayRequired', 'false');
  formData.append('detectOrientation', 'true');
  formData.append('scale', 'true');

  const ocrResponse = await fetch(OCR_API_URL, {
    method: 'POST',
    body: formData
  });

  if (!ocrResponse.ok) {
    throw new Error(`OCR API error: ${ocrResponse.status}`);
  }

  const result = await ocrResponse.json();

  if (result.IsErroredOnProcessing) {
    throw new Error('OCR processing failed');
  }

  if (!result.ParsedResults || result.ParsedResults.length === 0) {
    throw new Error('No text found in image');
  }

  const extractedText = result.ParsedResults[0].ParsedText;
  const expiryDate = extractExpiryDate(extractedText);

  return {
    text: extractedText,
    expiryDate: expiryDate
  };
};

export default performOCR;