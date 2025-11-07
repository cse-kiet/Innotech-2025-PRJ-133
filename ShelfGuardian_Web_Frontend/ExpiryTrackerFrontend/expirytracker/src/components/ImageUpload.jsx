import React, { useCallback, useState } from 'react';
import { useDropzone } from 'react-dropzone';
import { Upload, X, Image as ImageIcon, Scan, Loader } from 'lucide-react';
import performOCR from '../services/ocrService';

const ImageUpload = ({ image, onImageChange, onImageRemove, onExpiryDateExtracted }) => {
  const [isProcessing, setIsProcessing] = useState(false);
  const [ocrResult, setOcrResult] = useState(null);

  const processImageForOCR = async (imageDataUrl) => {
    if (!onExpiryDateExtracted) return;

    setIsProcessing(true);
    try {
      console.log('[ImageUpload] Starting OCR processing');
      const result = await performOCR(imageDataUrl);
      setOcrResult(result);

      if (result.expiryDate) {
        console.log('[ImageUpload] Expiry date extracted:', result.expiryDate);
        onExpiryDateExtracted(result.expiryDate);
      } else {
        console.log('[ImageUpload] No expiry date found in image');
      }
    } catch (error) {
      console.error('[ImageUpload] OCR failed:', error);
      setOcrResult({ error: error.message });
    } finally {
      setIsProcessing(false);
    }
  };

  const onDrop = useCallback((acceptedFiles) => {
    if (acceptedFiles.length > 0) {
      const file = acceptedFiles[0];
      const reader = new FileReader();
      reader.onload = () => {
        const imageDataUrl = reader.result;
        onImageChange(imageDataUrl);
        // Automatically process for OCR
        processImageForOCR(imageDataUrl);
      };
      reader.readAsDataURL(file);
    }
  }, [onImageChange, onExpiryDateExtracted]);

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    accept: {
      'image/*': ['.jpeg', '.jpg', '.png', '.gif', '.webp']
    },
    multiple: false
  });

  return (
    <div className="space-y-4">
      <h3 className="text-lg font-semibold text-gray-900">Product Image</h3>
      
      {image ? (
        <div className="relative">
          <div className="relative rounded-lg overflow-hidden border-2 border-gray-200">
            <img
              src={image}
              alt="Product"
              className="w-full h-48 object-cover"
            />
            <button
              onClick={onImageRemove}
              className="absolute top-2 right-2 bg-red-500 hover:bg-red-600 text-white rounded-full p-1 transition-colors duration-200"
            >
              <X className="w-4 h-4" />
            </button>
          </div>

          {/* OCR Processing Status */}
          <div className="mt-3 space-y-2">
            {isProcessing && (
              <div className="flex items-center space-x-2 text-blue-600">
                <Loader className="w-4 h-4 animate-spin" />
                <span className="text-sm">Scanning for expiry date...</span>
              </div>
            )}

            {ocrResult && !isProcessing && (
              <div className="space-y-2">
                {ocrResult.expiryDate ? (
                  <div className="flex items-center space-x-2 text-green-600">
                    <Scan className="w-4 h-4" />
                    <span className="text-sm font-medium">
                      Expiry date found: {new Date(ocrResult.expiryDate).toLocaleDateString()}
                    </span>
                  </div>
                ) : (
                  <div className="flex items-center space-x-2 text-orange-600">
                    <Scan className="w-4 h-4" />
                    <span className="text-sm">
                      {ocrResult.error ? `OCR failed: ${ocrResult.error}` : 'No expiry date detected in image'}
                    </span>
                  </div>
                )}
              </div>
            )}
          </div>

          <p className="text-sm text-gray-600 mt-2">
            Click the X to remove this image and upload a new one
          </p>
        </div>
      ) : (
        <div
          {...getRootProps()}
          className={`
            border-2 border-dashed rounded-lg p-8 text-center cursor-pointer transition-colors duration-200
            ${isDragActive 
              ? 'border-blue-500 bg-blue-50' 
              : 'border-gray-300 hover:border-gray-400 hover:bg-gray-50'
            }
          `}
        >
          <input {...getInputProps()} />
          <div className="space-y-4">
            <div className="mx-auto w-12 h-12 bg-gray-100 rounded-full flex items-center justify-center">
              {isDragActive ? (
                <Upload className="w-6 h-6 text-blue-600" />
              ) : (
                <ImageIcon className="w-6 h-6 text-gray-400" />
              )}
            </div>
            <div>
              <p className="text-lg font-medium text-gray-900">
                {isDragActive ? 'Drop the image here' : 'Upload product image'}
              </p>
              <p className="text-sm text-gray-600 mt-1">
                Drag & drop an image here, or click to select
              </p>
              <p className="text-xs text-gray-500 mt-2">
                Supports: JPEG, PNG, GIF, WebP
              </p>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ImageUpload;
