"use client";

import Image, { StaticImageData } from "next/image";
import React, { useState } from "react";

type Props = {
  placeholder: StaticImageData | string;
  onUploaded: (url: string) => void;
};

const ImageUploader: React.FC<Props> = ({ placeholder, onUploaded }) => {
  const [preview, setPreview] = useState<string | null>(null);
  const fileInputRef = React.useRef<HTMLInputElement>(null);

  const handleFileChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    const previewUrl = URL.createObjectURL(file);
    setPreview(previewUrl);

    const formData = new FormData();
    formData.append("file", file);

    try {
      const response = await fetch(
        `${process.env.NEXT_PUBLIC_API}/upload/images`,
        {
          method: "POST",
          body: formData,
        }
      );

      if (!response.ok) throw new Error("Upload failed");
      const data = await response.text();
      onUploaded(data);
    } catch (error) {
      console.error("Upload error", error);
      setPreview(null);
    }
  };

  const triggerFileSelect = () => {
    fileInputRef.current?.click();
  };

  return (
    <div>
      <input
        type="file"
        accept="image/*"
        ref={fileInputRef}
        onChange={handleFileChange}
        className="hidden"
      />
      <Image
        src={preview || placeholder}
        alt="Upload preview"
        width={400}
        height={400}
        className="cursor-pointer rounded-2xl"
        onClick={triggerFileSelect}
      />
    </div>
  );
};

export default ImageUploader;
