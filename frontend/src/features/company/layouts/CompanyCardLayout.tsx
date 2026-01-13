interface CompanyCardLayoutProps {
  header: React.ReactNode;
  content: React.ReactNode;
  footer: React.ReactNode;
}

export const CompanyCardLayout = ({ header, content, footer }: CompanyCardLayoutProps) => {
  return (
    <div className="border rounded-lg shadow-sm hover:shadow-md transition-shadow p-4 bg-white h-full flex flex-col justify-between">
      <div className="mb-4">{header}</div>
      <div className="flex-1">{content}</div>
      <div className="mt-4 pt-2 border-t">{footer}</div>
    </div>
  );
};