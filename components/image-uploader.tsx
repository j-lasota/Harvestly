"use client";

import Image, { StaticImageData } from "next/image";
import { useSession } from "next-auth/react";
import { useRef, useState } from "react";

type Props = {
  currImage?: string;
  placeholder: StaticImageData | string;
  onUploaded: (url: string) => void;
};

const ImageUploader: React.FC<Props> = ({
  currImage,
  placeholder,
  onUploaded,
}) => {
  const [preview, setPreview] = useState<string | null>(currImage || null);
  const fileInputRef = useRef<HTMLInputElement>(null);
  const { data: session } = useSession();
  const token = session?.accessToken;

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
          headers: {
            authorization: token ? `Bearer ${token}` : "",
          },
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
        className="cursor-pointer rounded-2xl shadow-md"
        onClick={triggerFileSelect}
      />
    </div>
  );
};

export default ImageUploader;
